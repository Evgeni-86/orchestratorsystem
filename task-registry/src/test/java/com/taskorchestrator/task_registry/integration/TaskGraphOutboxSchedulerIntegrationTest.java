package com.taskorchestrator.task_registry.integration;

import static com.taskorchestrator.task_registry.service.OutboxService.GRAPH_PROCESSING;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.taskorchestrator.task_registry.config.IntegrationTest;
import com.taskorchestrator.task_registry.domain.TaskGraphOutboxMessage;
import com.taskorchestrator.task_registry.enums.OutboxStatus;
import com.taskorchestrator.task_registry.scheduler.TaskGraphOutboxScheduler;
import com.taskorchestrator.task_registry.service.OutboxService;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@IntegrationTest
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class TaskGraphOutboxSchedulerIntegrationTest {

  @MockitoSpyBean
  private TaskGraphOutboxScheduler taskGraphOutboxScheduler;
  @Autowired
  private OutboxService outboxService;
  @Autowired
  private EntityManager entityManager;
  @Autowired
  private PlatformTransactionManager transactionManager;

  @BeforeEach
  void cleanDatabase() {
    TransactionStatus status = transactionManager.getTransaction(
        new DefaultTransactionDefinition());
    try {
      entityManager.createNativeQuery("DELETE FROM task_dependencies").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM graph_tasks").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM task_graphs").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM task_templates").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM task_templates_outbox").executeUpdate();
      transactionManager.commit(status);
    } catch (Exception e) {
      transactionManager.rollback(status);
      throw e;
    }
    entityManager.clear();
  }

  @Test
  void whenSchedulerRuns_ShouldProcessPendingMessagesAndUpdateStatus() throws Exception {
    // Arrange
    TaskGraphOutboxMessage message1 = createTestOutboxMessage(OutboxStatus.PENDING);
    TaskGraphOutboxMessage message2 = createTestOutboxMessage(OutboxStatus.PENDING);

    outboxService.save(message1);
    outboxService.save(message2);

    // Act
    // Запускаем шедулер вручную (имитируем выполнение)
    taskGraphOutboxScheduler.processOutboxMessage();

    // Assert
    // Ждем немного для обработки
    Thread.sleep(3000);

    // Проверяем, что метод был вызван
    verify(taskGraphOutboxScheduler, times(1)).processOutboxMessage();

    // Проверяем, что сообщения обновлены в БД
    List<TaskGraphOutboxMessage> updatedMessages =
        outboxService.findByTypeAndOutboxStatus(GRAPH_PROCESSING, OutboxStatus.COMPLETED);

    assertNotNull(updatedMessages);
    assertEquals(2, updatedMessages.size());

    // Проверяем, что у всех сообщений статус STARTED
    assertTrue(updatedMessages.stream()
        .allMatch(msg -> msg.getOutboxStatus() == OutboxStatus.COMPLETED));
  }

  @Test
  void testMultipleScheduledExecutions() {
    // Arrange
    for (int i = 0; i < 5; i++) {
      TaskGraphOutboxMessage message = createTestOutboxMessage(OutboxStatus.PENDING);
      outboxService.save(message);
    }

    // Act & Assert с использованием Awaitility
    await()
        .atMost(30, TimeUnit.SECONDS)
        .pollInterval(1, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          // Проверяем, что шедулер запускался несколько раз
          verify(taskGraphOutboxScheduler, atLeast(2)).processOutboxMessage();

          // Проверяем, что все сообщения обработаны
          List<TaskGraphOutboxMessage> pendingMessages =
              outboxService.findByTypeAndOutboxStatus(GRAPH_PROCESSING, OutboxStatus.PENDING);

          assertTrue(pendingMessages.isEmpty());
        });
  }

  private TaskGraphOutboxMessage createTestOutboxMessage(OutboxStatus status) {
    TaskGraphOutboxMessage message = new TaskGraphOutboxMessage();
    message.setCreatedAt(Instant.now().minusSeconds(1000));
    message.setType(GRAPH_PROCESSING);
    message.setPayload("{\"graphId\": \"graphId\", \"createdAt\": \"createdAt\"}");
    message.setOutboxStatus(status);
    return message;
  }
}
