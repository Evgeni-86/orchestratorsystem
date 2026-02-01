package com.taskorchestrator.task_registry_gex.application.core.port.in;

import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.graph.GraphValidateResultResponseDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.graph.TaskGraphCreateDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.graph.TaskGraphResponseDto;

public interface GraphService {

  TaskGraphResponseDto createTaskGraph(TaskGraphCreateDto taskGraphCreateDto);

  GraphValidateResultResponseDto validateGraphById(String id);

  TaskGraphResponseDto findById(String id);
}
