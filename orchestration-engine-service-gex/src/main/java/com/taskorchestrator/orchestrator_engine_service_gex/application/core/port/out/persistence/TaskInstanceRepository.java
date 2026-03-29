package com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.out.persistence;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity.TaskInstanceEntity;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain.enums.TaskStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskInstanceRepository {

  TaskInstanceEntity save(TaskInstanceEntity task);

  Optional<TaskInstanceEntity> findById(UUID id);

  List<TaskInstanceEntity> findByExecutionId(UUID executionId);

  List<TaskInstanceEntity> findByExecutionIdAndStatus(UUID executionId, TaskStatus status);

  List<TaskInstanceEntity> saveAll(List<TaskInstanceEntity> entities);
}
