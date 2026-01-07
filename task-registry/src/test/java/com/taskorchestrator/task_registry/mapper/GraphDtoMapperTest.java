package com.taskorchestrator.task_registry.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.taskorchestrator.task_registry.domain.TaskDependency;
import com.taskorchestrator.task_registry.domain.TaskGraph;
import com.taskorchestrator.task_registry.dto.graph.GraphDependencyDto;
import com.taskorchestrator.task_registry.dto.graph.TaskGraphCreateDto;
import com.taskorchestrator.task_registry.enums.TaskCondition;
import com.taskorchestrator.task_registry.mapper.graph.GraphDtoMapper;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class GraphDtoMapperTest {

  private final GraphDtoMapper mapper = Mappers.getMapper(GraphDtoMapper.class);

  @Test
  void shouldMapCreateDtoToDomain_WhenFullData() {
    // given
    UUID templateId1 = UUID.randomUUID();
    UUID templateId2 = UUID.randomUUID();

    TaskGraphCreateDto dto = new TaskGraphCreateDto(
        "Test Graph",
        List.of(templateId1, templateId2),
        List.of(
            new GraphDependencyDto(templateId1, templateId2, TaskCondition.SUCCESS)
        ),
        Map.of("priority", "high")
    );

    // when
    TaskGraph result = mapper.createDtoToDomain(dto);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Test Graph");
    assertThat(result.isValidated()).isFalse();
    assertThat(result.getTasks()).hasSize(2);
    assertThat(result.getTasks().get(0).getId()).isEqualTo(templateId1);
    assertThat(result.getTasks().get(1).getId()).isEqualTo(templateId2);
    assertThat(result.getDependencies()).hasSize(1);
    assertThat(result.getDependencies().get(0).getParentTaskId()).isEqualTo(templateId1);
    assertThat(result.getDependencies().get(0).getChildTaskId()).isEqualTo(templateId2);
    assertThat(result.getDependencies().get(0).getCondition()).isEqualTo(TaskCondition.SUCCESS);
  }

  @Test
  void shouldMapCreateDtoToDomain_WhenNoDependencies() {
    // given
    UUID templateId = UUID.randomUUID();
    TaskGraphCreateDto dto = new TaskGraphCreateDto(
        "Simple Graph",
        List.of(templateId)
    );

    // when
    TaskGraph result = mapper.createDtoToDomain(dto);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Simple Graph");
    assertThat(result.getTasks()).hasSize(1);
    assertThat(result.getTasks().get(0).getId()).isEqualTo(templateId);
    assertThat(result.getDependencies()).isEmpty();
  }

  @Test
  void shouldMapCreateDtoToDomain_WhenNullDependencies() {
    // given
    TaskGraphCreateDto dto = new TaskGraphCreateDto(
        "Test Graph",
        List.of(UUID.randomUUID()),
        null,  // явный null
        Map.of()
    );

    // when
    TaskGraph result = mapper.createDtoToDomain(dto);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getDependencies()).isNotNull().isEmpty();
  }

  @Test
  void shouldMapGraphDependencyDtoToTaskDependency() {
    // given
    UUID parentId = UUID.randomUUID();
    UUID childId = UUID.randomUUID();
    GraphDependencyDto dto = new GraphDependencyDto(parentId, childId, TaskCondition.ON_FAILURE);

    // when
    TaskDependency result = mapper.mapDependency(dto);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getParentTaskId()).isEqualTo(parentId);
    assertThat(result.getChildTaskId()).isEqualTo(childId);
    assertThat(result.getCondition()).isEqualTo(TaskCondition.ON_FAILURE);
  }

  @Test
  void shouldValidateSelfDependency_InDtoConstructor() {
    // given
    UUID sameId = UUID.randomUUID();

    // when & then
    // Проверяем, что конструктор DTO валидирует данные
    assertThatThrownBy(() ->
        new GraphDependencyDto(sameId, sameId, TaskCondition.SUCCESS)
    )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("cannot depend on itself");
  }

  @Test
  void shouldMapDependency_WithDifferentConditions() {
    // Проверяем все возможные значения TaskCondition
    List<TaskCondition> conditions = List.of(
        TaskCondition.SUCCESS,
        TaskCondition.ALWAYS,
        TaskCondition.ON_FAILURE
    );

    for (TaskCondition condition : conditions) {
      UUID parentId = UUID.randomUUID();
      UUID childId = UUID.randomUUID();
      GraphDependencyDto dto = new GraphDependencyDto(parentId, childId, condition);

      TaskDependency result = mapper.mapDependency(dto);

      assertThat(result.getCondition()).isEqualTo(condition);
    }
  }
}
