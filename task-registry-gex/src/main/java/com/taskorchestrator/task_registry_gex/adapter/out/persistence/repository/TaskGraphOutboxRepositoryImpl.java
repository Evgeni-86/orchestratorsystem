package com.taskorchestrator.task_registry_gex.adapter.out.persistence.repository;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskGraphOutboxEntity;
import com.taskorchestrator.task_registry_gex.application.core.domain.enums.OutboxStatus;
import com.taskorchestrator.task_registry_gex.application.core.port.out.TaskGraphOutboxJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TaskGraphOutboxRepositoryImpl implements TaskGraphOutboxJpaRepository {

  private final JpaTaskGraphOutboxJpaRepository jpaTaskGraphOutboxJpaRepository;

  @Override
  public TaskGraphOutboxEntity save(TaskGraphOutboxEntity taskGraphOutboxEntity) {
    return jpaTaskGraphOutboxJpaRepository.save(taskGraphOutboxEntity);
  }

  @Override
  public Optional<TaskGraphOutboxEntity> findById(UUID messageId) {
    return jpaTaskGraphOutboxJpaRepository.findById(messageId);
  }

  @Override
  public Optional<List<TaskGraphOutboxEntity>> findByTypeAndOutboxStatus(String type,
      OutboxStatus outboxStatus) {
    return jpaTaskGraphOutboxJpaRepository.findByTypeAndOutboxStatus(type, outboxStatus);
  }

  @Override
  public Optional<List<TaskGraphOutboxEntity>> findByType(String type) {
    return jpaTaskGraphOutboxJpaRepository.findByType(type);
  }

  @Override
  public void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
    jpaTaskGraphOutboxJpaRepository.deleteByTypeAndOutboxStatus(type, outboxStatus);
  }

  @Override
  public List<TaskGraphOutboxEntity> findByOutboxStatusWithLock(OutboxStatus outboxStatus,
      Pageable pageable) {
    return jpaTaskGraphOutboxJpaRepository.findByOutboxStatusWithLock(outboxStatus, pageable);
  }

  @Override
  public List<TaskGraphOutboxEntity> saveAll(List<TaskGraphOutboxEntity> entities) {
    return jpaTaskGraphOutboxJpaRepository.saveAll(entities);
  }
}
