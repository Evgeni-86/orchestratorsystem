package com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task;

import com.taskorchestrator.task_registry_gex.application.core.domain.enums.TaskType;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record TaskTemplateResponseDto(
    UUID id,
    String name,
    String version,
    TaskType type,
    Map<String, Object> inputSchema,
    Map<String, Object> outputSchema,
    Map<String, Object> config,
    Instant createdAt,
    Instant updatedAt
) {

}
