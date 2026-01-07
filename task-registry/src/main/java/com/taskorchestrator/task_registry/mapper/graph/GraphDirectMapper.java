package com.taskorchestrator.task_registry.mapper.graph;

import com.taskorchestrator.task_registry.config.CentralMapperConfig;
import com.taskorchestrator.task_registry.dto.graph.DependencyInfo;
import com.taskorchestrator.task_registry.dto.graph.TaskGraphResponseDto;
import com.taskorchestrator.task_registry.dto.graph.TemplateInfo;
import com.taskorchestrator.task_registry.entity.TaskDependencyEntity;
import com.taskorchestrator.task_registry.entity.TaskGraphEntity;
import com.taskorchestrator.task_registry.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry.enums.TaskType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface GraphDirectMapper {

  @Mapping(target = "templates", source = "templates")
  @Mapping(target = "dependencies", source = "dependencies")
  @Mapping(target = "metadata", ignore = true)
  @Mapping(target = "entryPointTaskIds", ignore = true)
    // У TaskGraphEntity нет metadata поля
  TaskGraphResponseDto entityToResponseDto(TaskGraphEntity entity);

  // Новый метод с дополнительным параметром
  @Mapping(target = "templates", source = "entity.templates")
  @Mapping(target = "dependencies", source = "entity.dependencies")
  @Mapping(target = "metadata", ignore = true)
  @Mapping(target = "entryPointTaskIds", source = "entryPointTaskIds")
  TaskGraphResponseDto entityToResponseDtoWithEntryPoint(
      TaskGraphEntity entity,
      List<UUID> entryPointTaskIds
  );

  // Маппинг TaskTemplateEntity → TemplateInfo
  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "version", source = "version")
  @Mapping(target = "type", source = "type")
  TemplateInfo templateEntityToTemplateInfo(TaskTemplateEntity entity);

  // MapStruct сам сгенерирует метод для списка
  List<TemplateInfo> mapTemplates(List<TaskTemplateEntity> templates);

  // Маппинг TaskDependencyEntity → DependencyInfo
  @Mapping(target = "parentTemplateId", source = "parent.id")
  @Mapping(target = "childTemplateId", source = "child.id")
  @Mapping(target = "condition", source = "condition")
  DependencyInfo dependencyEntityToDependencyInfo(TaskDependencyEntity entity);

  // MapStruct сам сгенерирует метод для списка
  List<DependencyInfo> mapDependencies(List<TaskDependencyEntity> dependencies);

  // Дефолтный метод для вычисления metadata (опционально)
  default Map<String, Object> computeMetadata(TaskGraphEntity entity) {
    if (entity == null) {
      return Map.of();
    }

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("tasksCount", entity.getTemplates() != null ? entity.getTemplates().size() : 0);
    metadata.put("dependenciesCount",
        entity.getDependencies() != null ? entity.getDependencies().size() : 0);

    // Можно добавить статистику по типам задач
    if (entity.getTemplates() != null) {
      Map<TaskType, Long> typeDistribution = entity.getTemplates().stream()
          .collect(Collectors.groupingBy(TaskTemplateEntity::getType, Collectors.counting()));
      metadata.put("taskTypeDistribution", typeDistribution);
    }

    return metadata;
  }
}
