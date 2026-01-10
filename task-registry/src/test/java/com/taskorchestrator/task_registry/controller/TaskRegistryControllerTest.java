package com.taskorchestrator.task_registry.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskorchestrator.task_registry.dto.ResponseDto;
import com.taskorchestrator.task_registry.dto.task.TaskTemplateCreateDto;
import com.taskorchestrator.task_registry.dto.task.TaskTemplateResponseDto;
import com.taskorchestrator.task_registry.enums.TaskType;
import com.taskorchestrator.task_registry.service.TaskTemplateService;
import com.taskorchestrator.task_registry.util.ResponseBuilder;
import com.taskorchestrator.task_registry.util.sort.tasktemplate.TaskTemplateSortResolver;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TaskRegistryController.class)
class TaskRegistryControllerTest {

  @Autowired
  private MockMvc mvc;
  @MockitoBean
  private TaskTemplateService taskTemplateService;
  @MockitoBean
  private ResponseBuilder responseBuilder;
  @MockitoBean
  private TaskTemplateSortResolver taskTemplateSortResolver;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void postNewTaskTemplateShouldWork() throws Exception {
    // Arrange
    TaskTemplateCreateDto dto = new TaskTemplateCreateDto(
        "name",
        "v1.0",
        TaskType.HTTP_CALL,
        Map.of("new", "schema"),
        null,
        null
    );

    UUID templateId = UUID.randomUUID();
    TaskTemplateResponseDto responseDto = new TaskTemplateResponseDto(
        templateId,
        "name",
        "v1.0",
        TaskType.HTTP_CALL,
        Map.of("new", "schema"),
        null,
        null,
        null,
        null
    );

    // Настраиваем моки
    when(taskTemplateService.createTaskTemplate(dto)).thenReturn(responseDto);
    when(responseBuilder.buildSingle(any())).thenAnswer(invocation -> {
      Object data = invocation.getArgument(0);
      return new ResponseDto<>(data, null);
    });

    // Act & Assert
    mvc.perform(post("/api/v1/templates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location",
            "http://localhost/api/v1/templates/" + templateId));

    verify(taskTemplateService).createTaskTemplate(dto);
    verify(responseBuilder).buildSingle(responseDto);
  }
}