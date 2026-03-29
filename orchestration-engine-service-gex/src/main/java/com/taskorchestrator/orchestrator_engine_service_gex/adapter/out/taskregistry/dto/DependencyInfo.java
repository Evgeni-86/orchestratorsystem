package com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.taskregistry.dto;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.taskregistry.enums.TaskCondition;
import java.util.UUID;

public record DependencyInfo(
    UUID parentTemplateId,
    UUID childTemplateId,
    TaskCondition condition
) {

}
