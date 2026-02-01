package com.taskorchestrator.task_registry_gex.adapter.in.web.controller;

import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.ResponseDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateCreateDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateFilterDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateResponseDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateUpdateDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.util.ResponseBuilder;
import com.taskorchestrator.task_registry_gex.adapter.out.persistence.util.PageableFactory;
import com.taskorchestrator.task_registry_gex.adapter.out.persistence.util.sort.tasktemplate.TaskTemplateSortResolver;
import com.taskorchestrator.task_registry_gex.application.core.port.in.TaskTemplateService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/templates")
public class TaskRegistryController extends BaseController {

  private final TaskTemplateService taskTemplateService;
  private final TaskTemplateSortResolver taskTemplateSortResolver;

  public TaskRegistryController(
      ResponseBuilder responseBuilder, TaskTemplateService taskTemplateService,
      TaskTemplateSortResolver taskTemplateSortResolver) {
    super(responseBuilder);
    this.taskTemplateService = taskTemplateService;
    this.taskTemplateSortResolver = taskTemplateSortResolver;
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<ResponseDto<TaskTemplateResponseDto>> createTaskTemplate(
      @RequestBody TaskTemplateCreateDto taskTemplateCreateDto
  ) {
    TaskTemplateResponseDto createTaskTemplateResponse = taskTemplateService.createTaskTemplate(
        taskTemplateCreateDto);
    return createResponse(createTaskTemplateResponse, createTaskTemplateResponse.id());
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResponseDto<List<TaskTemplateResponseDto>>> findAllTaskTemplate(
      TaskTemplateFilterDto filter,
      Pageable pageable,
      @RequestParam(name = "sort", required = false) String sortParam
  ) {
    Sort sort = taskTemplateSortResolver.resolve(sortParam);
    Pageable sortedPageable = PageableFactory.build(pageable, sort);
    Page<TaskTemplateResponseDto> pageableTaskTemplateResponse = taskTemplateService.findAllPage(
        filter, sortedPageable);
    return okResponse(pageableTaskTemplateResponse);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResponseDto<TaskTemplateResponseDto>> findTaskTemplate(
      @PathVariable String id
  ) {
    TaskTemplateResponseDto taskTemplateResponse = taskTemplateService.findById(id);
    return okResponse(taskTemplateResponse);
  }

  @PatchMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<ResponseDto<TaskTemplateResponseDto>> updateTaskTemplate(
      @PathVariable String id, @RequestBody TaskTemplateUpdateDto taskTemplateUpdateDto
  ) {
    TaskTemplateResponseDto updateTaskTemplateResponse = taskTemplateService.updateTaskTemplate(id,
        taskTemplateUpdateDto);
    return okResponse(updateTaskTemplateResponse);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> deleteTaskTemplate(@PathVariable String id) {
    taskTemplateService.deleteTaskTemplate(id);
    return noContentResponse();
  }
}
