package com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.in;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.in.rabbit.dto.TaskGraphEventPayloadDto;

public interface OrchestrationEngineService {

  void handleGraphCreatedEvent(TaskGraphEventPayloadDto event);
}
