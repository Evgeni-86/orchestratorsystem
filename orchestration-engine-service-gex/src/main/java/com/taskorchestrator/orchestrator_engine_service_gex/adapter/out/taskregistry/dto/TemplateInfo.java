package com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.taskregistry.dto;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.taskregistry.enums.TaskType;
import java.util.UUID;

public record TemplateInfo(
    UUID id,
    String name,
    String version,
    TaskType type
) {

}
