package com.taskorchestrator.task_registry.dto.graph;

import com.taskorchestrator.task_registry.enums.TaskCondition;
import java.util.UUID;

public record GraphDependencyDto(
    UUID parentTemplateId,  // соответствует parent в TaskDependencyEntity
    UUID childTemplateId,   // соответствует child в TaskDependencyEntity
    TaskCondition condition // SUCCESS, ALWAYS, ON_FAILURE
) {

  public GraphDependencyDto {
    if (parentTemplateId != null && parentTemplateId.equals(childTemplateId)) {
      throw new IllegalArgumentException("Task cannot depend on itself");
    }
  }
}
