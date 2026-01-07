package com.taskorchestrator.task_registry.dto.task;

import com.taskorchestrator.task_registry.enums.TaskType;
import java.util.Map;

public record TaskTemplateCreateDto(
    String name,
    String version,
    TaskType type,
    Map<String, Object> inputSchema,
    Map<String, Object> outputSchema,
    Map<String, Object> config
) {

}
