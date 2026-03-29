package com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.out.taskregistry;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.taskregistry.dto.TaskGraphDto;
import java.util.UUID;

public interface TaskRegistryClient {

  TaskGraphDto getGraph(UUID graphId);
}
