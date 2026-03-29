package com.taskorchestrator.task_registry_gex.adapter.out.persistence.repository;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry_gex.application.core.port.out.TaskTemplateRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TaskTemplateRepositoryImpl implements TaskTemplateRepository {

  private final JpaTaskTemplateRepository jpaTaskTemplateRepository;

  @Override
  public List<TaskTemplateEntity> findAllById(List<UUID> templateIds) {
    return jpaTaskTemplateRepository.findAllById(templateIds);
  }

  @Override
  public Page<TaskTemplateEntity> findAll(Specification<TaskTemplateEntity> specification,
      Pageable pageable) {
    return jpaTaskTemplateRepository.findAll(specification, pageable);
  }

  @Override
  public TaskTemplateEntity save(TaskTemplateEntity taskTemplate) {
    return jpaTaskTemplateRepository.save(taskTemplate);
  }

  @Override
  public List<TaskTemplateEntity> saveAll(List<TaskTemplateEntity> taskTemplateEntities) {
    return jpaTaskTemplateRepository.saveAll(taskTemplateEntities);
  }

  @Override
  public Optional<TaskTemplateEntity> findById(UUID uuid) {
    return jpaTaskTemplateRepository.findById(uuid);
  }

  @Override
  public void deleteById(UUID id) {
    jpaTaskTemplateRepository.deleteById(id);
  }
}
