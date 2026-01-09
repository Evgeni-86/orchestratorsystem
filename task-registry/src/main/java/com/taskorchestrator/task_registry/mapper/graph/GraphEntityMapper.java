package com.taskorchestrator.task_registry.mapper.graph;

import com.taskorchestrator.task_registry.config.CentralMapperConfig;
import com.taskorchestrator.task_registry.domain.TaskDependency;
import com.taskorchestrator.task_registry.domain.TaskGraph;
import com.taskorchestrator.task_registry.domain.TaskTemplate;
import com.taskorchestrator.task_registry.entity.TaskDependencyEntity;
import com.taskorchestrator.task_registry.entity.TaskGraphEntity;
import com.taskorchestrator.task_registry.entity.TaskTemplateEntity;
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
