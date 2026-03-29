package com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain;

import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "executionId")
public class ExecutionPlan {

  @Setter
  private UUID executionId;
  private Queue<UUID> readyQueue;    // Очередь задач в статусе READY (FIFO)
  private Set<UUID> completedTasks;  // Выполненные задачи
  private Set<UUID> failedTasks;     // Упавшие задачи
  private Set<UUID> runningTasks;    // Выполняющиеся прямо сейчас

  public void addToReady(UUID taskId) {
    readyQueue.offer(taskId);
  }

  public UUID pollReady() {
    return readyQueue.poll();
  }

  public void markRunning(UUID taskId) {
    runningTasks.add(taskId);
  }

  public void markCompleted(UUID taskId) {
    runningTasks.remove(taskId);
    completedTasks.add(taskId);
  }

  public void markFailed(UUID taskId) {
    runningTasks.remove(taskId);
    failedTasks.add(taskId);
  }

  public boolean hasReadyTasks() {
    return !readyQueue.isEmpty();
  }

  public boolean isComplete() {
    return runningTasks.isEmpty() && readyQueue.isEmpty();
  }
}
