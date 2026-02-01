package com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task;

import com.taskorchestrator.task_registry_gex.application.core.domain.enums.TaskType;
import java.util.Map;

public record TaskTemplateUpdateDto(
    String name,
    String version,
    TaskType type,
    Map<String, Object> inputSchema,
    Map<String, Object> outputSchema,
    Map<String, Object> config
) {

}
