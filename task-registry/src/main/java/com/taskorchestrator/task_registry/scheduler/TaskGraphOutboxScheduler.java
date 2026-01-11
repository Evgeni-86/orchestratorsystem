package com.taskorchestrator.task_registry.scheduler;

import com.taskorchestrator.task_registry.domain.TaskGraphOutboxMessage;
import com.taskorchestrator.task_registry.enums.OutboxStatus;
import com.taskorchestrator.task_registry.publisher.TaskGraphRequestMessagePublisher;
import com.taskorchestrator.task_registry.service.OutboxService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskGraphOutboxScheduler implements OutboxScheduler {

  private final OutboxService outboxService;
  private final TaskGraphRequestMessagePublisher taskGraphRequestMessagePublisher;

  public TaskGraphOutboxScheduler(OutboxService outboxService,
      TaskGraphRequestMessagePublisher taskGraphRequestMessagePublisher) {
    this.outboxService = outboxService;
    this.taskGraphRequestMessagePublisher = taskGraphRequestMessagePublisher;
  }

  @Scheduled(fixedDelayString = "${task-registry-service.outbox-scheduler-fixed-rate}",
      initialDelayString = "${task-registry-service.outbox-scheduler-initial-delay}")
  public void processOutboxMessage() {
    log.info("Processing outbox message STARTED !");
    List<TaskGraphOutboxMessage> reservedMessages = outboxService.reservePendingMessages(10);
    if (reservedMessages.isEmpty()) {
      log.info("No pending messages found");
      return;
    }
    log.info("Received {} TaskGraphOutboxMessage with ids: {}",
        reservedMessages.size(),
        reservedMessages.stream()
            .map(msg -> msg.getId().toString())
            .collect(Collectors.joining(",")));
    reservedMessages.forEach(msg -> {
      try {
        taskGraphRequestMessagePublisher.publish(msg, this::updateOutboxStatus);
      } catch (Exception e) {
        log.error("Failed to publish message {}", msg.getId(), e);
        // Компенсирующее действие
      }
    });
    log.info("Processing outbox message completed ! ");
  }

  private void updateOutboxStatus(TaskGraphOutboxMessage taskGraphOutboxMessage,
      OutboxStatus outboxStatus) {
    outboxService.updateStatusIfStarted(taskGraphOutboxMessage.getId(), outboxStatus);
    log.info("Outbox message id : {} updated successfully", taskGraphOutboxMessage.getId());
  }
}
