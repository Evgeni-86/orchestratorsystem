package com.taskorchestrator.task_registry_gex.application.core.domain;

import com.taskorchestrator.task_registry_gex.application.core.domain.enums.TaskCondition;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"parentTaskId", "childTaskId"})
public class TaskDependency {

  private UUID parentTaskId;  // ID задачи-предка
  private UUID childTaskId;   // ID задачи-потомка
  private TaskCondition condition;     // "SUCCESS", "ALWAYS", "ON_FAILURE" (пока только SUCCESS)
}

