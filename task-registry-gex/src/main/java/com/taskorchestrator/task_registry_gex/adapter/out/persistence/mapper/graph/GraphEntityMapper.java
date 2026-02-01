package com.taskorchestrator.task_registry_gex.adapter.out.persistence.mapper.graph;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskDependencyEntity;
import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskGraphEntity;
import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry_gex.application.core.domain.TaskDependency;
import com.taskorchestrator.task_registry_gex.application.core.domain.TaskGraph;
import com.taskorchestrator.task_registry_gex.application.core.domain.TaskTemplate;
import com.taskorchestrator.task_registry_gex.infrastructure.config.CentralMapperConfig;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface GraphEntityMapper {

  @Mapping(target = "tasks", source = "templates")
  TaskGraph toDomain(TaskGraphEntity taskGraphEntity);

  TaskTemplate templateToDomain(TaskTemplateEntity entity);

  List<TaskTemplate> templateListToDomain(List<TaskTemplateEntity> taskTemplateEntities);

  @Mapping(target = "parentTaskId", source = "parent.id")
  @Mapping(target = "childTaskId", source = "child.id")
  TaskDependency dependencyToDomain(TaskDependencyEntity entity);

  List<TaskDependency> dependencyListToDomain(List<TaskDependencyEntity> taskDependencyEntities);
}
