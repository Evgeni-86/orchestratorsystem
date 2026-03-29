package com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.taskregistry.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record TaskGraphDto(
    UUID id,
    String name,
    Instant createdAt,
    List<TemplateInfo> templates,
    List<DependencyInfo> dependencies,
    List<UUID> entryPointTaskIds,
    Map<String, Object> metadata
) {

}
