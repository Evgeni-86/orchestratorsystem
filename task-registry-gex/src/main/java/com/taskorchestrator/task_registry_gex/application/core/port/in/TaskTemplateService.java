package com.taskorchestrator.task_registry_gex.application.core.port.in;

import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateCreateDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateFilterDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateResponseDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskTemplateService {

  TaskTemplateResponseDto createTaskTemplate(TaskTemplateCreateDto taskTemplateCreateDto);

  Page<TaskTemplateResponseDto> findAllPage(TaskTemplateFilterDto filter, Pageable sortedPageable);

  TaskTemplateResponseDto findById(String id);

  TaskTemplateResponseDto updateTaskTemplate(String id, TaskTemplateUpdateDto taskTemplateUpdateDto);

  void deleteTaskTemplate(String id);
}
