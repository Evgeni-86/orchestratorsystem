package com.taskorchestrator.task_registry_gex.application.core.port.out;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskGraphEntity;
import java.util.Optional;
import java.util.UUID;

public interface TaskGraphRepository {

  Optional<TaskGraphEntity> findWithFullRelationsById(UUID uuid);

  Optional<TaskGraphEntity> findById(UUID uuid);

  TaskGraphEntity save(TaskGraphEntity entity);
}
