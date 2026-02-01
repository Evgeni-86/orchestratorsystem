package com.taskorchestrator.task_registry_gex.adapter.in.web.dto.graph;

import com.taskorchestrator.task_registry_gex.application.core.domain.enums.TaskCondition;
import java.util.UUID;

public record DependencyInfo(
    UUID parentTemplateId,
    UUID childTemplateId,
    TaskCondition condition
) {

}
