package com.taskorchestrator.task_registry_gex.adapter.out.persistence.repository;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskGraphEntity;
import com.taskorchestrator.task_registry_gex.application.core.port.out.TaskGraphRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class TaskGraphRepositoryImpl implements TaskGraphRepository {

  private final JpaTaskGraphRepository jpaTaskGraphRepository;

  @Override
  public Optional<TaskGraphEntity> findWithFullRelationsById(UUID uuid) {
    return jpaTaskGraphRepository.findWithFullRelationsById(uuid);
  }

  @Override
  public Optional<TaskGraphEntity> findById(UUID uuid) {
    return jpaTaskGraphRepository.findById(uuid);
  }

  @Override
  public List<TaskGraphEntity> findAllWithFullRelations() {
    return jpaTaskGraphRepository.findAllWithFullRelations();
  }

  @Override
  public TaskGraphEntity save(TaskGraphEntity entity) {
    return jpaTaskGraphRepository.save(entity);
  }

  @Override
  public void deleteById(UUID graphId) {
    jpaTaskGraphRepository.deleteById(graphId);
  }
}
