package com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain;

import com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain.enums.ExecutionStatus;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class WorkflowExecution {

  private UUID id;                    // UUID execution
  private UUID graphId;               // Ссылка на граф из TRS
  private ExecutionStatus status;       // PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
  private Map<String, Object> input;   // Входные данные для workflow
  private Map<String, Object> output;  // Финальный результат
  private Instant startedAt;
  private Instant finishedAt;
  private String errorMessage;

  public void start() {
    this.status = ExecutionStatus.RUNNING;
    this.startedAt = Instant.now();
  }

  public void complete(Map<String, Object> output) {
    this.status = ExecutionStatus.COMPLETED;
    this.output = output;
    this.finishedAt = Instant.now();
  }

  public void fail(String errorMessage) {
    this.status = ExecutionStatus.FAILED;
    this.errorMessage = errorMessage;
    this.finishedAt = Instant.now();
  }

  public void cancel() {
    this.status = ExecutionStatus.CANCELLED;
    this.finishedAt = Instant.now();
  }
}
