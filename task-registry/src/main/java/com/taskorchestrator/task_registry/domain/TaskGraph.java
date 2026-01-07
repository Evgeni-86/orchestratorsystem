package com.taskorchestrator.task_registry.domain;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class TaskGraph {

  private UUID id;
  private String name;
  private List<TaskTemplate> tasks;
  private List<TaskDependency> dependencies;
  private UUID entryPointTaskId; // ID стартовой задачи
  private boolean validated;       // Прошел ли проверку на циклы
}
