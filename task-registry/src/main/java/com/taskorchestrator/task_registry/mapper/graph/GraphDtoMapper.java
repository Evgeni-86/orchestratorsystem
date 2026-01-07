package com.taskorchestrator.task_registry.mapper.graph;

import com.taskorchestrator.task_registry.config.CentralMapperConfig;
import com.taskorchestrator.task_registry.domain.TaskDependency;
import com.taskorchestrator.task_registry.domain.TaskGraph;
import com.taskorchestrator.task_registry.domain.TaskTemplate;
import com.taskorchestrator.task_registry.dto.graph.GraphDependencyDto;
import com.taskorchestrator.task_registry.dto.graph.TaskGraphCreateDto;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface GraphDtoMapper {

  @Mapping(target = "id", ignore = true) // генерируется при сохранении
  @Mapping(target = "entryPointTaskId", ignore = true) // вычисляется отдельно
  @Mapping(target = "validated", constant = "false")
  @Mapping(target = "tasks", source = "taskTemplateIds")
  TaskGraph createDtoToDomain(TaskGraphCreateDto dto);

  // Маппинг List<UUID> → List<TaskTemplate>
  default List<TaskTemplate> mapTaskTemplateIds(List<UUID> templateIds) {
    if (templateIds == null) {
      return new ArrayList<>();
    }
    return templateIds.stream()
        .map(id -> {
          TaskTemplate template = new TaskTemplate();
          template.setId(id);
          return template;
        }).toList();
  }

  // Маппинг GraphDependencyDto → TaskDependency
  @Mapping(target = "parentTaskId", source = "parentTemplateId")
  @Mapping(target = "childTaskId", source = "childTemplateId")
  TaskDependency mapDependency(GraphDependencyDto dto);

  List<TaskDependency> mapDependencies(List<GraphDependencyDto> dependencies);

  // Гарантируем, что коллекции никогда не null
  @AfterMapping
  default void ensureCollectionsNotNull(@MappingTarget TaskGraph taskGraph) {
    if (taskGraph == null) {
      return;
    }

    // Инициализируем tasks если null
    if (taskGraph.getTasks() == null) {
      taskGraph.setTasks(new ArrayList<>());
    }

    // Инициализируем dependencies если null
    if (taskGraph.getDependencies() == null) {
      taskGraph.setDependencies(new ArrayList<>());
    }
  }
}
