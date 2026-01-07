package com.taskorchestrator.task_registry.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class TaskGraph {

  private String id;
  private String name;
  private List<TaskTemplate> tasks;
  private List<TaskDependency> dependencies;
  private String entryPointTaskId; // ID стартовой задачи
  private boolean validated;       // Прошел ли проверку на циклы
}
