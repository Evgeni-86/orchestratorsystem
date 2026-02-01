package com.taskorchestrator.task_registry_gex.application.core.port.in;

import com.taskorchestrator.task_registry_gex.application.core.domain.TaskGraphOutboxMessage;
import com.taskorchestrator.task_registry_gex.application.core.domain.enums.OutboxStatus;
import java.util.List;
import java.util.UUID;

public interface OutboxService {

  List<TaskGraphOutboxMessage> reservePendingMessages(int i);

  void updateStatusIfStarted(UUID id, OutboxStatus outboxStatus);
}
