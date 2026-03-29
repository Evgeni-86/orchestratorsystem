package com.taskorchestrator.orchestrator_engine_service_gex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.in.rabbit.TaskGraphEventListener;
import com.taskorchestrator.orchestrator_engine_service_gex.adapter.in.rabbit.dto.TaskGraphEventPayloadDto;
import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity.InputTaskGraphRabbitMessageEntity;
import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.repository.JpaInputBoxMessageRepository;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.out.persistence.InputBoxMessageRepository;
import com.taskorchestrator.orchestrator_engine_service_gex.config.containers.TestContainersConfig;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.containers.RabbitMQContainer;

@SpringBootTest
@ImportTestcontainers(TestContainersConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class TaskGraphEventListenerTest {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private RabbitMQContainer rabbitMQContainer;

  @Autowired
  private JpaInputBoxMessageRepository jpaInputBoxMessageRepository;

//  @BeforeEach
//  void setUp() throws Exception {
//    // Создаем очередь перед каждым тестом
//    rabbitMQContainer.execInContainer(
//        "rabbitmqadmin", "declare", "queue",
//        "name=task_graph_queue", "durable=true"
//    );
//  }

  @Test
  void testSendFiveMessages() {
    // Создаем 5 тестовых сообщений
    List<TaskGraphEventPayloadDto> messages = IntStream.range(1, 6)
        .mapToObj(i -> TaskGraphEventPayloadDto.builder()
            .graphId("graph-" + i)
            .createdAt(Instant.now().toString())
            .build())
        .toList();

    // Отправляем
    messages.forEach(message -> {
      rabbitTemplate.convertAndSend("", "task_graph_queue", message);
      System.out.println("Sent: " + message);
    });

    // Ожидаем появления каждого сообщения в БД
    for (int i = 1; i <= 5; i++) {
      String graphId = "graph-" + i;

      await()
          .atMost(10, TimeUnit.SECONDS)
          .pollInterval(500, TimeUnit.MILLISECONDS)
          .until(() -> jpaInputBoxMessageRepository.findByGraphId(graphId).isPresent());

      Optional<InputTaskGraphRabbitMessageEntity> found =
          jpaInputBoxMessageRepository.findByGraphId(graphId);

      assertThat(found).isPresent();
      assertThat(found.get().getGraphId()).isEqualTo(graphId);
      assertThat(found.get().getCreatedAt()).isNotNull();

      System.out.println("Verified: " + graphId);
    }
  }

  @Test
  void testConcurrentInsert() throws Exception {
    String graphId = "concurrent-graph";
    ExecutorService executor = Executors.newFixedThreadPool(10);

    // Подготавливаем 10 потоков с одним и тем же graphId
    List<Callable<Integer>> tasks = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      final int index = i;
      tasks.add(() -> {
        InputTaskGraphRabbitMessageEntity entity = new InputTaskGraphRabbitMessageEntity();
        entity.setId(UUID.randomUUID());
        entity.setGraphId(graphId);
        entity.setCreatedAt(Instant.now());

        // Добавляем небольшую задержку для усиления конкурентности
        if (index % 3 == 0) {
          Thread.sleep(1);
        }

        int result = jpaInputBoxMessageRepository.insertIfNotExists(entity);
        System.out.println("Thread " + index + " result: " + result);
        return result;
      });
    }

    // Запускаем все потоки одновременно
    List<Future<Integer>> futures = executor.invokeAll(tasks);

    // Считаем успешные вставки
    long insertedCount = futures.stream()
        .map(future -> {
          try {
            return future.get();
          } catch (Exception e) {
            return 0;
          }
        })
        .filter(result -> result == 1)
        .count();

    // Должна быть только ОДНА успешная вставка
    assertThat(insertedCount).isEqualTo(1);

    // В БД должна быть только ОДНА запись
    long dbCount = jpaInputBoxMessageRepository.findByGraphId(graphId).stream().count();
    assertThat(dbCount).isEqualTo(1);
  }
}
