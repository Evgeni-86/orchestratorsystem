package com.taskorchestrator.task_registry_gex.application.core.port.out;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskTemplateEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface TaskTemplateRepository {

  List<TaskTemplateEntity> findAllById(List<UUID> templateIds);

  Page<TaskTemplateEntity> findAll(Specification<TaskTemplateEntity> specification, Pageable pageable);

  TaskTemplateEntity save(TaskTemplateEntity taskTemplate);

  Optional<TaskTemplateEntity> findById(UUID uuid);

  void deleteById(UUID id);
}
