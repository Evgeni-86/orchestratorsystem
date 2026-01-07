package com.taskorchestrator.task_registry.dto.graph;

import com.taskorchestrator.task_registry.enums.TaskCondition;
import java.util.UUID;

public record DependencyInfo(
    UUID parentTemplateId,
    UUID childTemplateId,
    TaskCondition condition
) {

}
