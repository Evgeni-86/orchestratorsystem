package com.taskorchestrator.task_registry.dto.graph;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record TaskGraphCreateDto(
    String name,
    List<UUID> taskTemplateIds,
    List<GraphDependencyDto> dependencies,
    Map<String, Object> metadata
) {

  public TaskGraphCreateDto(String name, List<UUID> taskTemplateIds) {
    this(name, taskTemplateIds, List.of(), Map.of());
  }

  public TaskGraphCreateDto(String name, List<UUID> taskTemplateIds,
      List<GraphDependencyDto> dependencies) {
    this(name, taskTemplateIds, dependencies, Map.of());
  }
}
