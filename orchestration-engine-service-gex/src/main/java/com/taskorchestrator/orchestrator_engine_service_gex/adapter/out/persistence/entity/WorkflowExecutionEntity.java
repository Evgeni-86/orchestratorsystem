package com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity;

import com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain.enums.ExecutionStatus;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain.enums.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "workflow_execution")
@Entity
public class WorkflowExecutionEntity {

  @Id
  @UuidGenerator
  @Column(name = "id", updatable = false)
  private UUID id;

  @Column(name = "graph_id", nullable = false)
  private UUID graphId;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private ExecutionStatus status;

  @Column(name = "input", columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, Object> input;

  @Column(name = "output", columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, Object> output;

  @Column(name = "started_at")
  private Instant startedAt;

  @Column(name = "finished_at")
  private Instant finishedAt;

  @Column(name = "error_message")
  private String errorMessage;
}
