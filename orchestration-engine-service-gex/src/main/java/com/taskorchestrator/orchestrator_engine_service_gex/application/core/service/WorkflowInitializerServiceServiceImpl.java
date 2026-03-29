package com.taskorchestrator.orchestrator_engine_service_gex.application.core.service;

import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity.TaskInstanceEntity;
import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity.WorkflowExecutionEntity;
import com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.taskregistry.dto.TaskGraphDto;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain.ExecutionPlan;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain.TaskInstance;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.domain.WorkflowExecution;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.in.WorkflowInitializerService;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.out.persistence.TaskInstanceRepository;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.out.persistence.WorkflowExecutionRepository;
import com.taskorchestrator.orchestrator_engine_service_gex.application.core.port.out.taskregistry.TaskRegistryClient;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowInitializerServiceServiceImpl implements WorkflowInitializerService {

  private final TaskRegistryClient taskRegistryClient;
  private final WorkflowExecutionRepository workflowExecutionRepository;
  private final TaskInstanceRepository taskInstanceRepository;

  public ExecutionPlan initialize(UUID graphId, Map<String, Object> workflowInput) {
    // 1. Получить граф из Task Registry
    TaskGraphDto graph = taskRegistryClient.getGraph(graphId);

    // 2. Создать WorkflowExecution
    WorkflowExecution execution = createWorkflowExecution(graphId, workflowInput);

    // 3. Создать TaskInstance для каждой задачи
    List<TaskInstance> tasks = createTaskInstances(execution.getId(), graph);

    // 4. Найти начальные задачи (dependsOn пуст)
    List<TaskInstance> initialTasks = tasks.stream()
        .filter(TaskInstance::isReady)
        .collect(Collectors.toList());

    // 5. Перевести начальные задачи в READY
    initialTasks.forEach(TaskInstance::markReady);

    // 6. Сохранить все в БД
    WorkflowExecutionEntity workflowExecution = new WorkflowExecutionEntity();
    List<TaskInstanceEntity> taskInstances = List.of(new TaskInstanceEntity());
    workflowExecutionRepository.save(workflowExecution);
    taskInstanceRepository.saveAll(taskInstances);

    // 7. Построить ExecutionPlan
    ExecutionPlan plan = new ExecutionPlan();
    plan.setExecutionId(execution.getId());
    initialTasks.forEach(task -> plan.addToReady(task.getId()));

    return plan;
  }

  private WorkflowExecution createWorkflowExecution(UUID graphId,
      Map<String, Object> workflowInput) {
    return null;
  }

  private List<TaskInstance> createTaskInstances(UUID id, TaskGraphDto graph) {
    return Collections.emptyList();
  }
}
