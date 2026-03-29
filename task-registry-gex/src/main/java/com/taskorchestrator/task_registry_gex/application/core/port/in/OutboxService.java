package com.taskorchestrator.task_registry_gex.application.core.port.in;

import com.taskorchestrator.task_registry_gex.application.core.domain.TaskGraphOutboxMessage;
import com.taskorchestrator.task_registry_gex.application.core.domain.enums.OutboxStatus;
import java.util.List;
import java.util.UUID;

public interface OutboxService {

  TaskGraphOutboxMessage save(TaskGraphOutboxMessage taskGraphOutboxMessage);

  List<TaskGraphOutboxMessage> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

  List<TaskGraphOutboxMessage> findByType(String type);

  void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

  List<TaskGraphOutboxMessage> reservePendingMessages(int i);

  void updateStatusIfStarted(UUID id, OutboxStatus outboxStatus);
}
