package com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.repository;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity.TaskInstanceEntity;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain.enums.TaskStatus;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.out.persistence.TaskInstanceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TaskInstanceRepositoryImpl implements TaskInstanceRepository {

  private final JpaTaskInstanceRepository jpaTaskInstanceRepository;

  @Override
  public TaskInstanceEntity save(TaskInstanceEntity task) {
    return jpaTaskInstanceRepository.save(task);
  }

  @Override
  public Optional<TaskInstanceEntity> findById(UUID id) {
    return jpaTaskInstanceRepository.findById(id);
  }

  @Override
  public List<TaskInstanceEntity> findByExecutionId(UUID executionId) {
    return jpaTaskInstanceRepository.findByExecutionId(executionId);
  }

  @Override
  public List<TaskInstanceEntity> findByExecutionIdAndStatus(UUID executionId, TaskStatus status) {
    return jpaTaskInstanceRepository.findByExecutionIdAndStatus(executionId, status);
  }

  @Override
  public List<TaskInstanceEntity> saveAll(List<TaskInstanceEntity> entities) {
    return jpaTaskInstanceRepository.saveAll(entities);
  }
}
