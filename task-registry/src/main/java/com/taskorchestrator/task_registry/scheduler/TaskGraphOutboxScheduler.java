package com.taskorchestrator.task_registry.scheduler;

import com.taskorchestrator.task_registry.domain.TaskGraphOutboxMessage;
import com.taskorchestrator.task_registry.enums.OutboxStatus;
import com.taskorchestrator.task_registry.publisher.TaskGraphRequestMessagePublisher;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class TaskGraphOutboxScheduler implements OutboxScheduler {

  private final TaskGraphOutboxHelper taskGraphOutboxHelper;
  private final TaskGraphRequestMessagePublisher taskGraphRequestMessagePublisher;

  public TaskGraphOutboxScheduler(TaskGraphOutboxHelper taskGraphOutboxHelper,
      TaskGraphRequestMessagePublisher taskGraphRequestMessagePublisher) {
    this.taskGraphOutboxHelper = taskGraphOutboxHelper;
    this.taskGraphRequestMessagePublisher = taskGraphRequestMessagePublisher;
  }

  @Override
  @Transactional
  @Scheduled(fixedDelayString = "${task-registry-service.outbox-scheduler-fixed-rate}",
      initialDelayString = "${task-registry-service.outbox-scheduler-initial-delay}")
  public void processOutboxMessage() {

    log.info("Processing outbox message STARTED !");

    var outboxMessageResponse =
        taskGraphOutboxHelper.getTaskGraphOutboxMessageByOutboxMessageStatus(OutboxStatus.PENDING);

    log.info("Received {} TaskGraphOutboxMessage with ids :  {} , sending message bus !",
        outboxMessageResponse.size(),
        outboxMessageResponse.stream()
            .map(taskGraphOutboxMessage -> taskGraphOutboxMessage.getId().toString())
            .collect(Collectors.joining(",")));
    outboxMessageResponse.forEach(
        taskGraphOutboxMessage ->
        {
          taskGraphOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);
          taskGraphOutboxHelper.save(taskGraphOutboxMessage);
          taskGraphRequestMessagePublisher.publish(taskGraphOutboxMessage,
              this::updateOutboxStatus);
        }
    );
    log.info("Processing outbox message completed ! ");
  }

  private void updateOutboxStatus(TaskGraphOutboxMessage taskGraphOutboxMessage,
      OutboxStatus outboxStatus) {
    taskGraphOutboxMessage.setOutboxStatus(outboxStatus);
    taskGraphOutboxHelper.save(taskGraphOutboxMessage);
    log.info("Outbox message id : {} updated successfully", taskGraphOutboxMessage.getId());
  }
}
