package com.taskorchestrator.task_registry_gex.adapter.in.web.dto.graph;

import com.taskorchestrator.task_registry_gex.application.core.domain.enums.TaskType;
import java.util.UUID;

public record TemplateInfo(
    UUID id,
    String name,
    String version,
    TaskType type
) {

}
