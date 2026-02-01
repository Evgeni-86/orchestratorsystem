package com.taskorchestrator.task_registry_gex.adapter.in.web.controller;

import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.ResponseDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.graph.GraphValidateResultResponseDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.graph.TaskGraphCreateDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.graph.TaskGraphResponseDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.util.ResponseBuilder;
import com.taskorchestrator.task_registry_gex.application.core.port.in.GraphService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/graphs")
public class GraphController extends BaseController {

  public final GraphService graphService;

  public GraphController(ResponseBuilder responseBuilder, GraphService graphService) {
    super(responseBuilder);
    this.graphService = graphService;
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<ResponseDto<TaskGraphResponseDto>> createGraph(
      @RequestBody TaskGraphCreateDto taskGraphCreateDto
  ) {
    TaskGraphResponseDto createTaskGraphResponse = graphService.createTaskGraph(taskGraphCreateDto);
    return createResponse(createTaskGraphResponse, createTaskGraphResponse.id());
  }

  @GetMapping(path = "/{id}/validate", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResponseDto<GraphValidateResultResponseDto>> validateGraph(
      @PathVariable String id) {
    GraphValidateResultResponseDto graphValidateResponseDto = graphService.validateGraphById(
        id);
    return okResponse(graphValidateResponseDto);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResponseDto<TaskGraphResponseDto>> findGraph(@PathVariable String id) {
    TaskGraphResponseDto graphResponse = graphService.findById(id);
    return okResponse(graphResponse);
  }
}
