package com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.out.persistence;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity.WorkflowExecutionEntity;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain.WorkflowExecution;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowExecutionRepository {

  WorkflowExecutionEntity save(WorkflowExecutionEntity execution);

  Optional<WorkflowExecutionEntity> findById(UUID id);

  Optional<WorkflowExecutionEntity> findByGraphId(UUID graphId);
}
