package com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.repository;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity.TaskInstanceEntity;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain.TaskInstance;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain.enums.TaskStatus;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.out.persistence.TaskInstanceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTaskInstanceRepository extends JpaRepository<TaskInstanceEntity, UUID> {

  List<TaskInstanceEntity> findByExecutionId(UUID executionId);

  List<TaskInstanceEntity> findByExecutionIdAndStatus(UUID executionId, TaskStatus status);
}
