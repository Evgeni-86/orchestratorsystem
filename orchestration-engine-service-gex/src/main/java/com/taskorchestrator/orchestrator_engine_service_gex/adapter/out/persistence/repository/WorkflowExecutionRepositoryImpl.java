package com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.repository;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity.WorkflowExecutionEntity;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.out.persistence.WorkflowExecutionRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WorkflowExecutionRepositoryImpl implements WorkflowExecutionRepository {

  private final JpaWorkflowExecutionRepository jpaWorkflowExecutionRepository;

  @Override
  public WorkflowExecutionEntity save(WorkflowExecutionEntity execution) {
    return jpaWorkflowExecutionRepository.save(execution);
  }

  @Override
  public Optional<WorkflowExecutionEntity> findById(UUID id) {
    return jpaWorkflowExecutionRepository.findById(id);
  }

  @Override
  public Optional<WorkflowExecutionEntity> findByGraphId(UUID graphId) {
    return jpaWorkflowExecutionRepository.findByGraphId(graphId);
  }
}
