package com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.taskregistry;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.taskregistry.dto.TaskGraphDto;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.out.taskregistry.TaskRegistryClient;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TaskRegistryRestClientService implements TaskRegistryClient {

  @Override
  public TaskGraphDto getGraph(UUID graphId) {
    return null;
  }
}
