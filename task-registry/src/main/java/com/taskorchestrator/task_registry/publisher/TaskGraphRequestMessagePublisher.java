package com.taskorchestrator.task_registry.publisher;

import com.taskorchestrator.task_registry.domain.TaskGraphOutboxMessage;
import com.taskorchestrator.task_registry.enums.OutboxStatus;
import java.util.function.BiConsumer;

public interface TaskGraphRequestMessagePublisher {

  void publish(TaskGraphOutboxMessage message,
      BiConsumer<TaskGraphOutboxMessage, OutboxStatus> outboxCallback);
}
