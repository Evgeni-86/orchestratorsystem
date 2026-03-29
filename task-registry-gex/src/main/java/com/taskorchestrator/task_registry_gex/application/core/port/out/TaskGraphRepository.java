package com.taskorchestrator.task_registry_gex.application.core.port.out;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskGraphEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskGraphRepository {

  Optional<TaskGraphEntity> findWithFullRelationsById(UUID uuid);

  List<TaskGraphEntity> findAllWithFullRelations();

  Optional<TaskGraphEntity> findById(UUID uuid);

  TaskGraphEntity save(TaskGraphEntity entity);

  void deleteById(UUID graphId);
}
