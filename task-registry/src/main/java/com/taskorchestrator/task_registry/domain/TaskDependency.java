package com.taskorchestrator.task_registry.domain;

import com.taskorchestrator.task_registry.enums.TaskCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"parentTaskId", "childTaskId"})
public class TaskDependency {

  private final String parentTaskId;  // ID задачи-предка
  private final String childTaskId;   // ID задачи-потомка
  private TaskCondition condition;     // "SUCCESS", "ALWAYS", "ON_FAILURE" (пока только SUCCESS)
}

