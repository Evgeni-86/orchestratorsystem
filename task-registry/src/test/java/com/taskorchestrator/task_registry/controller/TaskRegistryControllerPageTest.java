package com.taskorchestrator.task_registry.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.taskorchestrator.task_registry.dto.ResponseDto;
import com.taskorchestrator.task_registry.dto.meta.MetaResponseDto;
import com.taskorchestrator.task_registry.dto.task.TaskTemplateResponseDto;
import com.taskorchestrator.task_registry.enums.TaskType;
import com.taskorchestrator.task_registry.service.TaskTemplateService;
import com.taskorchestrator.task_registry.util.ResponseBuilder;
import com.taskorchestrator.task_registry.util.sort.tasktemplate.TaskTemplateSortResolver;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TaskRegistryController.class)
class TaskRegistryControllerPageTest {

  @Autowired
  private MockMvc mvc;
  @MockitoBean
  private TaskTemplateService taskTemplateService;
  @MockitoBean
  private TaskTemplateSortResolver taskTemplateSortResolver;
  @MockitoBean
  private ResponseBuilder responseBuilder;

  @BeforeEach
  void setUp() {
    // Настраиваем мок ResponseBuilder для возврата правильной структуры
    when(responseBuilder.buildPaged(any(Page.class)))
        .thenAnswer(invocation -> {
          Page<?> page = invocation.getArgument(0);
          MetaResponseDto meta = new MetaResponseDto(
              page.isFirst(),
              page.isLast(),
              page.getNumberOfElements(),
              page.getNumber(),
              page.getSize(),
              page.getTotalPages()
          );
          return new ResponseDto<>(page.getContent(), meta);
        });
  }

  @Test
  void findAllTaskTemplate_withValidParams_shouldReturnOk() throws Exception {
    // Arrange
    String type = "SCRIPT";
    int page = 0;
    int size = 10;
    String sortParam = "NEW";

    Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
    Pageable sortedPageable = PageRequest.of(page, size, sort);

    TaskTemplateResponseDto dto = new TaskTemplateResponseDto(
        UUID.randomUUID(), "Test", "1.0", TaskType.SCRIPT,
        Map.of(), Map.of(), Map.of(), Instant.now(), Instant.now()
    );

    Page<TaskTemplateResponseDto> pageResult = new PageImpl<>(
        List.of(dto), sortedPageable, 1
    );

    when(taskTemplateSortResolver.resolve(sortParam)).thenReturn(sort);
    when(taskTemplateService.findAllPage(
        argThat(filter -> type.equals(filter.type())),
        eq(sortedPageable)
    )).thenReturn(pageResult);

    // Act & Assert
    mvc.perform(get("/api/v1/templates")
            .param("type", type)
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(size))
            .param("sort", sortParam))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].name").value("Test"))
        .andExpect(jsonPath("$.meta.pageNumber").value(page))
        .andExpect(jsonPath("$.meta.pageSize").value(size));
  }

  @Test
  void findAllTaskTemplate_withoutSort_shouldCallResolverWithNull() throws Exception {
    // Arrange
    Sort defaultSort = Sort.by(Sort.Direction.DESC, "createdAt");
    when(taskTemplateSortResolver.resolve(null)).thenReturn(defaultSort);
    when(taskTemplateService.findAllPage(any(), any()))
        .thenReturn(Page.empty());

    // Act & Assert
    mvc.perform(get("/api/v1/templates")
            .param("type", "FEATURE"))
        .andExpect(status().isOk());

    verify(taskTemplateSortResolver).resolve(null);
  }

  @Test
  void findAllTaskTemplate_withDifferentPageable_shouldPassCorrectParams() throws Exception {
    // Arrange
    when(taskTemplateSortResolver.resolve(any())).thenReturn(Sort.unsorted());
    when(taskTemplateService.findAllPage(any(), any()))
        .thenReturn(Page.empty());

    // Act
    mvc.perform(get("/api/v1/templates")
            .param("page", "2")
            .param("size", "50"))
        .andExpect(status().isOk());

    // Assert
    verify(taskTemplateService).findAllPage(
        any(),
        argThat(pageable ->
            pageable.getPageNumber() == 2 &&
                pageable.getPageSize() == 50
        )
    );
  }
}
