package com.taskorchestrator.task_registry_gex.controller;

import com.taskorchestrator.task_registry_gex.adapter.in.web.controller.GraphController;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.ResponseDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.graph.DependencyInfo;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.graph.TaskGraphResponseDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.graph.TemplateInfo;
import com.taskorchestrator.task_registry_gex.adapter.in.web.mapper.MetaMapper;
import com.taskorchestrator.task_registry_gex.adapter.in.web.util.ResponseBuilder;
import com.taskorchestrator.task_registry_gex.application.core.domain.enums.TaskCondition;
import com.taskorchestrator.task_registry_gex.application.core.domain.enums.TaskType;
import com.taskorchestrator.task_registry_gex.application.core.port.in.GraphService;
import com.taskorchestrator.task_registry_gex.infrastructure.exception.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GraphController.class)
class GraphControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockitoBean
  private GraphService graphService;
  @MockitoBean
  private ResponseBuilder responseBuilder;
  // MetaMapper мокается, т.к. используется в ResponseBuilder
  @MockitoBean
  private MetaMapper metaMapper;

  @Test
  void findGraph_shouldReturnGraphWhenExists() throws Exception {
    // Given
    String graphId = "550e8400-e29b-41d4-a716-446655440000";

    TaskGraphResponseDto responseDto = new TaskGraphResponseDto(
        UUID.fromString(graphId),
        "Test Graph",
        Instant.now(),
        List.of(new TemplateInfo(UUID.randomUUID(), "Task 1", "v1.0", TaskType.HTTP_CALL)),
        List.of(new DependencyInfo(UUID.randomUUID(), UUID.randomUUID(), TaskCondition.SUCCESS)),
        List.of(UUID.randomUUID()),
        Map.of("version", "1.0")
    );

    ResponseDto<TaskGraphResponseDto> response = new ResponseDto<>(
        responseDto,
        null
    );

    when(graphService.findById(graphId)).thenReturn(responseDto);
    when(responseBuilder.buildSingle(responseDto)).thenReturn(response);

    // When & Then
    mockMvc.perform(get("/api/v1/graphs/{id}", graphId)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.id").value(graphId))
        .andExpect(jsonPath("$.data.name").value("Test Graph"))
        .andExpect(jsonPath("$.data.templates").isArray())
        .andExpect(jsonPath("$.data.templates[0].name").value("Task 1"))
        .andExpect(jsonPath("$.data.dependencies").isArray())
        .andExpect(jsonPath("$.data.entryPointTaskIds").isArray())
        .andExpect(jsonPath("$.meta").doesNotExist());

    verify(graphService).findById(graphId);
    verify(responseBuilder).buildSingle(responseDto);
  }

  @Test
  void findGraph_shouldReturn404WhenGraphNotFound() throws Exception {
    String graphId = "550e8400-e29b-41d4-a716-446655440000";

    when(graphService.findById(graphId))
        .thenThrow(new ObjectNotFoundException("Graph not found"));

    mockMvc.perform(get("/api/v1/graphs/{id}", graphId))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("Graph not found"));

    verify(graphService).findById(graphId);
    verifyNoInteractions(responseBuilder);
  }

  @Test
  void findGraph_shouldHandleEmptyGraph() throws Exception {
    // Given
    String graphId = "550e8400-e29b-41d4-a716-446655440000";

    TaskGraphResponseDto responseDto = new TaskGraphResponseDto(
        UUID.fromString(graphId),
        "Empty Graph",
        Instant.now(),
        List.of(), // пустые списки
        List.of(),
        List.of(),
        Map.of()
    );

    ResponseDto<TaskGraphResponseDto> response = new ResponseDto<>(responseDto, null);

    when(graphService.findById(graphId)).thenReturn(responseDto);
    when(responseBuilder.buildSingle(responseDto)).thenReturn(response);

    // When & Then
    mockMvc.perform(get("/api/v1/graphs/{id}", graphId)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(graphId))
        .andExpect(jsonPath("$.data.name").value("Empty Graph"))
        .andExpect(jsonPath("$.data.templates").isArray())
        .andExpect(jsonPath("$.data.templates").isEmpty())
        .andExpect(jsonPath("$.data.dependencies").isArray())
        .andExpect(jsonPath("$.data.dependencies").isEmpty())
        .andExpect(jsonPath("$.data.entryPointTaskIds").isArray())
        .andExpect(jsonPath("$.data.entryPointTaskIds").isEmpty());

    verify(graphService).findById(graphId);
    verify(responseBuilder).buildSingle(responseDto);
  }

  @Test
  void findGraph_shouldReturnCorrectContentType() throws Exception {
    // Given
    String graphId = "550e8400-e29b-41d4-a716-446655440000";
    TaskGraphResponseDto responseDto = new TaskGraphResponseDto(
        UUID.fromString(graphId),
        "Test",
        Instant.now(),
        List.of(),
        List.of(),
        List.of(),
        Map.of()
    );
    ResponseDto<TaskGraphResponseDto> response = new ResponseDto<>(responseDto, null);

    when(graphService.findById(graphId)).thenReturn(responseDto);
    when(responseBuilder.buildSingle(responseDto)).thenReturn(response);

    // When & Then - проверяем различные Accept-заголовки
    mockMvc.perform(get("/api/v1/graphs/{id}", graphId)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    // Проверяем, что без Accept заголовка тоже работает
    mockMvc.perform(get("/api/v1/graphs/{id}", graphId))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void findGraph_shouldCallServiceWithCorrectId() throws Exception {
    // Given
    String graphId = "550e8400-e29b-41d4-a716-446655440000";
    TaskGraphResponseDto responseDto = new TaskGraphResponseDto(
        UUID.fromString(graphId),
        "Test",
        Instant.now(),
        List.of(),
        List.of(),
        List.of(),
        Map.of()
    );
    ResponseDto<TaskGraphResponseDto> response = new ResponseDto<>(responseDto, null);

    when(graphService.findById(graphId)).thenReturn(responseDto);
    when(responseBuilder.buildSingle(responseDto)).thenReturn(response);

    // When
    mockMvc.perform(get("/api/v1/graphs/{id}", graphId)
        .accept(MediaType.APPLICATION_JSON));

    // Then - проверяем, что сервис вызван с правильным ID
    verify(graphService).findById(graphId);
    verifyNoMoreInteractions(graphService);
  }

  @Test
  void findGraph_shouldNotRequireAuthentication() throws Exception {
    // Given
    String graphId = "550e8400-e29b-41d4-a716-446655440000";
    TaskGraphResponseDto responseDto = new TaskGraphResponseDto(
        UUID.fromString(graphId),
        "Test",
        Instant.now(),
        List.of(),
        List.of(),
        List.of(),
        Map.of()
    );
    ResponseDto<TaskGraphResponseDto> response = new ResponseDto<>(responseDto, null);

    when(graphService.findById(graphId)).thenReturn(responseDto);
    when(responseBuilder.buildSingle(responseDto)).thenReturn(response);

    // When & Then - запрос без авторизации должен проходить
    mockMvc.perform(get("/api/v1/graphs/{id}", graphId))
        .andExpect(status().isOk());
  }

  @Test
  void findGraph_shouldReturn500WhenInvalidUUID() throws Exception {
    String invalidId = "not-a-uuid";

    when(graphService.findById(invalidId))
        .thenThrow(new IllegalArgumentException("Invalid UUID string: " + invalidId));

    mockMvc.perform(get("/api/v1/graphs/{id}", invalidId))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.message").value("Произошла неизвестная ошибка."));

    verify(graphService).findById(invalidId);
  }
}