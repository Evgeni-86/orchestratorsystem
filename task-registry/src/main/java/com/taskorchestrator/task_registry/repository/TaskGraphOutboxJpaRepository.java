package com.taskorchestrator.task_registry.repository;

import com.taskorchestrator.task_registry.entity.TaskGraphOutboxEntity;
import com.taskorchestrator.task_registry.enums.OutboxStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskGraphOutboxJpaRepository extends
    JpaRepository<TaskGraphOutboxEntity, UUID> {

  Optional<List<TaskGraphOutboxEntity>> findByTypeAndOutboxStatus(String type,
      OutboxStatus outboxStatus);

  Optional<TaskGraphOutboxEntity> findByType(String type);

  void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);
}
