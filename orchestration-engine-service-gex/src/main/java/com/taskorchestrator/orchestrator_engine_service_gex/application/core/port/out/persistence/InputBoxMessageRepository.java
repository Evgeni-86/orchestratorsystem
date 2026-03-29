package com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.out.persistence;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity.InputTaskGraphRabbitMessageEntity;
import java.util.Optional;

public interface InputBoxMessageRepository {

  Optional<InputTaskGraphRabbitMessageEntity> findByGraphId(String graphId);

  int insertIfNotExists(InputTaskGraphRabbitMessageEntity inputTaskGraphRabbitMessageEntity);
}
