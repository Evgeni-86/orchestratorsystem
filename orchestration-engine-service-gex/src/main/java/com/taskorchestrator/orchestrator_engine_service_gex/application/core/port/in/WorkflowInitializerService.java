package com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.in;

import com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain.ExecutionPlan;
import java.util.Map;
import java.util.UUID;

public interface WorkflowInitializerService {

  ExecutionPlan initialize(UUID graphId, Map<String, Object> workflowInput);
}
