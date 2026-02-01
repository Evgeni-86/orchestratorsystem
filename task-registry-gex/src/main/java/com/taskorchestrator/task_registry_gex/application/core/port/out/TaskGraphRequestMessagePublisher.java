package com.taskorchestrator.task_registry_gex.application.core.port.out;

import com.taskorchestrator.task_registry_gex.application.core.domain.TaskGraphOutboxMessage;
import com.taskorchestrator.task_registry_gex.application.core.domain.enums.OutboxStatus;
import java.util.function.BiConsumer;

public interface TaskGraphRequestMessagePublisher {

  void publish(TaskGraphOutboxMessage message,
      BiConsumer<TaskGraphOutboxMessage, OutboxStatus> outboxCallback);
}
