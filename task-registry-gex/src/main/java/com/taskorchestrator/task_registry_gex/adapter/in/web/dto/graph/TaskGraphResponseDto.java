package com.taskorchestrator.task_registry_gex.adapter.in.web.dto.graph;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record TaskGraphResponseDto(
    UUID id,
    String name,
    Instant createdAt,
    List<TemplateInfo> templates,
    List<DependencyInfo> dependencies,
    List<UUID> entryPointTaskIds,
    Map<String, Object> metadata
) {

}