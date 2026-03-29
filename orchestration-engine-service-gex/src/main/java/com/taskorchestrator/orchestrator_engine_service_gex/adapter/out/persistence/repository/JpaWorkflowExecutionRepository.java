package com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.repository;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity.WorkflowExecutionEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaWorkflowExecutionRepository extends
    JpaRepository<WorkflowExecutionEntity, UUID> {

  Optional<WorkflowExecutionEntity> findByGraphId(UUID graphId);
}
