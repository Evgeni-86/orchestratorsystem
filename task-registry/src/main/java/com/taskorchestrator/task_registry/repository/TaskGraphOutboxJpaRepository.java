package com.taskorchestrator.task_registry.repository;

import com.taskorchestrator.task_registry.entity.TaskGraphOutboxEntity;
import com.taskorchestrator.task_registry.enums.OutboxStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskGraphOutboxJpaRepository extends
    JpaRepository<TaskGraphOutboxEntity, UUID> {

  Optional<List<TaskGraphOutboxEntity>> findByTypeAndOutboxStatus(String type,
      OutboxStatus outboxStatus);

  Optional<List<TaskGraphOutboxEntity>> findByType(String type);

  void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT e FROM TaskGraphOutboxEntity e " +
      "WHERE e.outboxStatus = :status " +
      "ORDER BY e.createdAt ASC")
  List<TaskGraphOutboxEntity> findByOutboxStatusWithLock(@Param("status") OutboxStatus status,
      Pageable pageable);
}
