package com.taskorchestrator.task_registry_gex.application.core.port.out;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskGraphOutboxEntity;
import com.taskorchestrator.task_registry_gex.application.core.domain.enums.OutboxStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface TaskGraphOutboxJpaRepository {

  TaskGraphOutboxEntity save(TaskGraphOutboxEntity taskGraphOutboxEntity);

  Optional<TaskGraphOutboxEntity> findById(UUID messageId);

  Optional<List<TaskGraphOutboxEntity>> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

  Optional<List<TaskGraphOutboxEntity>> findByType(String type);

  void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

  List<TaskGraphOutboxEntity> findByOutboxStatusWithLock(OutboxStatus outboxStatus, Pageable pageable);

  List<TaskGraphOutboxEntity> saveAll(List<TaskGraphOutboxEntity> entities);
}
