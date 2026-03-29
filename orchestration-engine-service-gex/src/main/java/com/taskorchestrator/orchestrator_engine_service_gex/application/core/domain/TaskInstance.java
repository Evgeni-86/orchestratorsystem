package com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain;

import com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain.enums.TaskStatus;
import java.time.Instant;
import java.util.List;
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
public class TaskInstance {

  private UUID id;                    // UUID
  private UUID executionId;           // Ссылка на WorkflowExecution
  private String templateId;            // Ссылка на TaskTemplate
  private TaskStatus status;            // PENDING, READY, RUNNING, COMPLETED, FAILED
  private Map<String, Object> input;   // Входные данные (могут включать результаты предков)
  private Map<String, Object> output;  // Результат выполнения
  private List<String> dependsOn;      // TaskInstance IDs от которых зависит
  private List<String> children;       // TaskInstance IDs которые зависят от этой
  private Instant createdAt;
  private Instant startedAt;
  private Instant completedAt;

  public void markReady() {
    this.status = TaskStatus.READY;
  }

  public void markRunning() {
    this.status = TaskStatus.RUNNING;
    this.startedAt = Instant.now();
  }

  public void complete(Map<String, Object> result) {
    this.status = TaskStatus.COMPLETED;
    this.output = result;
    this.completedAt = Instant.now();
  }

  public void fail(String errorMessage) {
    this.status = TaskStatus.FAILED;
    this.completedAt = Instant.now();
  }

  public boolean isReady() {
    return this.status == TaskStatus.PENDING && (dependsOn == null || dependsOn.isEmpty());
  }
}
