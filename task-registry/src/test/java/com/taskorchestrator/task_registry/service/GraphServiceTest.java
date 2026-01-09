package com.taskorchestrator.task_registry.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.taskorchestrator.task_registry.domain.TaskDependency;
import com.taskorchestrator.task_registry.domain.TaskGraph;
import com.taskorchestrator.task_registry.domain.TaskTemplate;
import com.taskorchestrator.task_registry.domain.validator.GraphValidator;
import com.taskorchestrator.task_registry.domain.validator.ValidationResult;
import com.taskorchestrator.task_registry.dto.graph.GraphValidateResultResponseDto;
import com.taskorchestrator.task_registry.entity.TaskDependencyEntity;
import com.taskorchestrator.task_registry.entity.TaskGraphEntity;
import com.taskorchestrator.task_registry.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry.enums.TaskCondition;
import com.taskorchestrator.task_registry.exception.ObjectNotFoundException;
import com.taskorchestrator.task_registry.mapper.graph.GraphDtoMapper;
import com.taskorchestrator.task_registry.mapper.graph.GraphEntityMapper;
import com.taskorchestrator.task_registry.repository.TaskGraphRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GraphServiceTest {

  @Mock
  private TaskGraphRepository taskGraphRepository;
  @Mock
  private GraphEntityMapper graphEntityMapper;
  @Mock
  private GraphDtoMapper graphDtoMapper;

  @InjectMocks
  private GraphService graphService;

  private final GraphValidator graphValidator = new GraphValidator();

  // Test data
  private UUID testGraphId;
  private String testGraphIdString;
  private TaskGraphEntity testGraphEntity;
  private TaskGraph testTaskGraph;
  private TaskGraphEntity cyclicGraphEntity;
  private TaskGraph cyclicTaskGraph;
  private TaskGraphEntity emptyGraphEntity;
  private TaskGraph emptyTaskGraph;

  @BeforeEach
  void setUp() {
    // Инициализация тестовых данных
    testGraphId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    testGraphIdString = testGraphId.toString();

    // Создание тестового графа (валидного)
    testGraphEntity = createTestGraphEntity();
    testTaskGraph = createTestTaskGraph(testGraphId);

    // Создание графа с циклической зависимостью
    cyclicGraphEntity = createCyclicGraphEntity();
    cyclicTaskGraph = createCyclicTaskGraph(testGraphId);

    // Создание пустого графа
    emptyGraphEntity = createEmptyGraphEntity();
    emptyTaskGraph = createEmptyTaskGraph(testGraphId);

    // Настройка GraphValidator в сервисе через reflection
    setGraphValidatorField(graphService, graphValidator);
  }

  @Test
  void validateGraphById_shouldReturnValidationResultWhenGraphExists() {
    // Given
    when(taskGraphRepository.findById(testGraphId)).thenReturn(Optional.of(testGraphEntity));
    when(graphEntityMapper.toDomain(testGraphEntity)).thenReturn(testTaskGraph);

    GraphValidateResultResponseDto responseDto =
        new GraphValidateResultResponseDto(true, List.of(), List.of("Test warning"));
    when(graphDtoMapper.validateToResponseDto(any(ValidationResult.class))).thenReturn(responseDto);

    // When
    GraphValidateResultResponseDto result = graphService.validateGraphById(testGraphIdString);

    // Then
    assertNotNull(result);
    assertThat(result.isValid()).isTrue();
    assertThat(result.warnings()).contains("Test warning");

    verify(taskGraphRepository).findById(testGraphId);
    verify(graphEntityMapper).toDomain(testGraphEntity);
    verify(graphDtoMapper).validateToResponseDto(any(ValidationResult.class));
  }

  @Test
  void validateGraphById_shouldThrowExceptionWhenGraphNotFound() {
    // Given
    when(taskGraphRepository.findById(testGraphId)).thenReturn(Optional.empty());

    // When & Then
    ObjectNotFoundException exception = assertThrows(
        ObjectNotFoundException.class,
        () -> graphService.validateGraphById(testGraphIdString)
    );

    assertThat(exception.getMessage()).contains("Graph not found");
    verify(taskGraphRepository).findById(testGraphId);
    verifyNoInteractions(graphEntityMapper, graphDtoMapper);
  }

  @Test
  void validateGraphById_shouldHandleInvalidUUID() {
    // Given
    String invalidId = "not-a-uuid";

    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> graphService.validateGraphById(invalidId)
    );

    verifyNoInteractions(taskGraphRepository, graphEntityMapper, graphDtoMapper);
  }

  @Test
  void validateGraphById_shouldMapValidationErrorsCorrectly() {
    // Given
    when(taskGraphRepository.findById(testGraphId)).thenReturn(Optional.of(cyclicGraphEntity));
    when(graphEntityMapper.toDomain(cyclicGraphEntity)).thenReturn(cyclicTaskGraph);

    GraphValidateResultResponseDto responseDto =
        new GraphValidateResultResponseDto(false, List.of("Graph contains cyclic dependencies"),
            List.of());
    when(graphDtoMapper.validateToResponseDto(any(ValidationResult.class))).thenReturn(responseDto);

    // When
    GraphValidateResultResponseDto result = graphService.validateGraphById(testGraphIdString);

    // Then
    assertNotNull(result);
    assertThat(result.isValid()).isFalse();
    assertThat(result.errors()).contains("Graph contains cyclic dependencies");

    verify(taskGraphRepository).findById(testGraphId);
    verify(graphEntityMapper).toDomain(cyclicGraphEntity);
    verify(graphDtoMapper).validateToResponseDto(any(ValidationResult.class));
  }

  @Test
  void validateGraphById_shouldHandleEmptyGraph() {
    // Given
    when(taskGraphRepository.findById(testGraphId)).thenReturn(Optional.of(emptyGraphEntity));
    when(graphEntityMapper.toDomain(emptyGraphEntity)).thenReturn(emptyTaskGraph);

    GraphValidateResultResponseDto responseDto =
        new GraphValidateResultResponseDto(false, List.of("Graph has no tasks"), List.of());
    when(graphDtoMapper.validateToResponseDto(any(ValidationResult.class))).thenReturn(responseDto);

    // When
    GraphValidateResultResponseDto result = graphService.validateGraphById(testGraphIdString);

    // Then
    assertNotNull(result);
    assertThat(result.isValid()).isFalse();
    assertThat(result.errors()).contains("Graph has no tasks");

    verify(taskGraphRepository).findById(testGraphId);
    verify(graphEntityMapper).toDomain(emptyGraphEntity);
    verify(graphDtoMapper).validateToResponseDto(any(ValidationResult.class));
  }

  // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

  private TaskGraphEntity createTestGraphEntity() {
    TaskGraphEntity entity = new TaskGraphEntity();
    entity.setName("Test Graph");

    TaskTemplateEntity template1 = new TaskTemplateEntity();
    template1.setId(UUID.randomUUID());
    template1.setName("Task 1");

    TaskTemplateEntity template2 = new TaskTemplateEntity();
    template2.setId(UUID.randomUUID());
    template2.setName("Task 2");

    entity.setTemplates(Arrays.asList(template1, template2));

    TaskDependencyEntity dependency = new TaskDependencyEntity();
    dependency.setParent(template1);
    dependency.setChild(template2);
    dependency.setCondition(TaskCondition.SUCCESS);

    entity.setDependencies(Collections.singletonList(dependency));

    return entity;
  }

  private TaskGraph createTestTaskGraph(UUID id) {
    TaskGraph graph = new TaskGraph(id, "Test Graph", new ArrayList<>(), new ArrayList<>(),
        null, false
    );

    TaskTemplate template1 = new TaskTemplate();
    template1.setId(UUID.randomUUID());
    template1.setName("Task 1");

    TaskTemplate template2 = new TaskTemplate();
    template2.setId(UUID.randomUUID());
    template2.setName("Task 2");

    graph.setTasks(Arrays.asList(template1, template2));

    TaskDependency dependency = new TaskDependency(template1.getId(), template2.getId(),
        TaskCondition.SUCCESS);

    graph.setDependencies(Collections.singletonList(dependency));

    return graph;
  }

  private TaskGraphEntity createCyclicGraphEntity() {
    TaskGraphEntity entity = new TaskGraphEntity();
    entity.setName("Cyclic Graph");

    TaskTemplateEntity template1 = new TaskTemplateEntity();
    template1.setId(UUID.randomUUID());
    template1.setName("Task A");

    TaskTemplateEntity template2 = new TaskTemplateEntity();
    template2.setId(UUID.randomUUID());
    template2.setName("Task B");

    entity.setTemplates(Arrays.asList(template1, template2));

    // Создаем циклическую зависимость
    TaskDependencyEntity dep1 = new TaskDependencyEntity();
    dep1.setParent(template1);
    dep1.setChild(template2);
    dep1.setCondition(TaskCondition.SUCCESS);

    TaskDependencyEntity dep2 = new TaskDependencyEntity();
    dep2.setParent(template2);
    dep2.setChild(template1);
    dep2.setCondition(TaskCondition.ALWAYS);

    entity.setDependencies(Arrays.asList(dep1, dep2));

    return entity;
  }

  private TaskGraph createCyclicTaskGraph(UUID id) {
    TaskGraph graph = new TaskGraph(id, "Cyclic Graph", new ArrayList<>(), new ArrayList<>(),
        null, false
    );

    TaskTemplate template1 = new TaskTemplate();
    template1.setId(UUID.randomUUID());
    template1.setName("Task A");

    TaskTemplate template2 = new TaskTemplate();
    template2.setId(UUID.randomUUID());
    template2.setName("Task B");

    graph.setTasks(Arrays.asList(template1, template2));

    TaskDependency dep1 = new TaskDependency(template1.getId(), template2.getId(),
        TaskCondition.SUCCESS);

    TaskDependency dep2 = new TaskDependency(template2.getId(), template1.getId(),
        TaskCondition.ALWAYS);

    graph.setDependencies(Arrays.asList(dep1, dep2));

    return graph;
  }

  private TaskGraphEntity createEmptyGraphEntity() {
    TaskGraphEntity entity = new TaskGraphEntity();
    entity.setName("Empty Graph");
    entity.setTemplates(new ArrayList<>());
    entity.setDependencies(new ArrayList<>());
    return entity;
  }

  private TaskGraph createEmptyTaskGraph(UUID id) {
    return new TaskGraph(id, "Empty Graph", new ArrayList<>(), new ArrayList<>(),
        null, false);
  }

  private void setGraphValidatorField(GraphService service, GraphValidator validator) {
    try {
      var field = GraphService.class.getDeclaredField("graphValidator");
      field.setAccessible(true);
      field.set(service, validator);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set graphValidator field", e);
    }
  }
}
