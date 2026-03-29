package com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.repository;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity.InputTaskGraphRabbitMessageEntity;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.out.persistence.InputBoxMessageRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JpaInputBoxMessageRepository extends
    JpaRepository<InputTaskGraphRabbitMessageEntity, UUID>, InputBoxMessageRepository {

  @Transactional
  @Modifying
  @Query(value = """
      INSERT INTO input_message_rabbitmq (id, graph_id, created_at)
      VALUES (:#{#entity.id}, :#{#entity.graphId}, :#{#entity.createdAt})
      ON CONFLICT (graph_id) DO NOTHING
      """, nativeQuery = true)
  int insertIfNotExists(
      @Param("entity") InputTaskGraphRabbitMessageEntity inputTaskGraphRabbitMessageEntity);
}
