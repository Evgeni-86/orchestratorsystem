package com.taskorchestrator.task_registry_gex.adapter.in.web.dto.graph;

import com.taskorchestrator.task_registry_gex.application.core.domain.enums.TaskCondition;
import java.util.UUID;

public record TaskDependencyDto(
    UUID parentTemplateId,  // соответствует parent в TaskDependencyEntity
    UUID childTemplateId,   // соответствует child в TaskDependencyEntity
    TaskCondition condition // SUCCESS, ALWAYS, ON_FAILURE
) {

  public TaskDependencyDto {
    if (parentTemplateId != null && parentTemplateId.equals(childTemplateId)) {
      throw new IllegalArgumentException("Task cannot depend on itself");
    }
  }
}
