package com.taskorchestrator.task_registry.dto.graph;

import com.taskorchestrator.task_registry.enums.TaskType;
import java.util.UUID;

public record TemplateInfo(
    UUID id,
    String name,
    String version,
    TaskType type
) {

}
