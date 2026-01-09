package com.taskorchestrator.task_registry.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.taskorchestrator.task_registry.domain.TaskDependency;
import com.taskorchestrator.task_registry.domain.TaskGraph;
import com.taskorchestrator.task_registry.domain.TaskTemplate;
import com.taskorchestrator.task_registry.entity.TaskDependencyEntity;
import com.taskorchestrator.task_registry.entity.TaskGraphEntity;
import com.taskorchestrator.task_registry.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry.enums.TaskCondition;
import com.taskorchestrator.task_registry.enums.TaskType;
import com.taskorchestrator.task_registry.mapper.graph.GraphEntityMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class GraphEntityMapperTest {

  private final GraphEntityMapper mapper = Mappers.getMapper(GraphEntityMapper.class);
  private TaskTemplateEntity templateEntity1;
  private TaskTemplateEntity templateEntity2;
  private TaskDependencyEntity dependencyEntity;
  private TaskGraphEntity graphEntity;

  @BeforeEach
  void setUp() {
    // Создание тестовых данных через конструкторы и сеттеры
    templateEntity1 = new TaskTemplateEntity();
    templateEntity1.setId(UUID.randomUUID());
    templateEntity1.setName("template-1");
    templateEntity1.setVersion("v1.0");
    templateEntity1.setType(TaskType.HTTP_CALL);
    templateEntity1.setInputSchema(Map.of("url", "string"));
    templateEntity1.setOutputSchema(Map.of("response", "object"));
    templateEntity1.setConfig(Map.of("timeout", 5000));
    templateEntity1.setCreatedAt(Instant.now().minusSeconds(3600));
    templateEntity1.setUpdatedAt(Instant.now());

    templateEntity2 = new TaskTemplateEntity();
    templateEntity2.setId(UUID.randomUUID());
    templateEntity2.setName("template-2");
    templateEntity2.setVersion("v1.0");
    templateEntity2.setType(TaskType.SCRIPT);
    templateEntity2.setInputSchema(Map.of("code", "string"));
    templateEntity2.setOutputSchema(Map.of("result", "object"));
    templateEntity2.setConfig(Map.of("language", "javascript"));
    templateEntity2.setCreatedAt(Instant.now().minusSeconds(1800));
    templateEntity2.setUpdatedAt(Instant.now());

    dependencyEntity = new TaskDependencyEntity();
    dependencyEntity.setParent(templateEntity1);
    dependencyEntity.setChild(templateEntity2);
    dependencyEntity.setCondition(TaskCondition.SUCCESS);

    graphEntity = new TaskGraphEntity();
    graphEntity.setName("Test Graph");
    graphEntity.setTemplates(new ArrayList<>(Arrays.asList(templateEntity1, templateEntity2)));
    graphEntity.setDependencies(new ArrayList<>(Collections.singletonList(dependencyEntity)));

    // Установка created через рефлексию или через @PrePersist в тесте
    try {
      var field = TaskGraphEntity.class.getDeclaredField("createdAt");
      field.setAccessible(true);
      field.set(graphEntity, Instant.now());
    } catch (Exception e) {
      // Игнорируем, если не можем установить
    }
  }

  @Test
  void templateToDomain_shouldMapAllFieldsCorrectly() {
    // When
    TaskTemplate result = mapper.templateToDomain(templateEntity1);

    // Then
    assertNotNull(result);
    assertThat(result.getId()).isEqualTo(templateEntity1.getId());
    assertThat(result.getName()).isEqualTo(templateEntity1.getName());
    assertThat(result.getVersion()).isEqualTo(templateEntity1.getVersion());
    assertThat(result.getType()).isEqualTo(templateEntity1.getType());
    assertThat(result.getInputSchema()).isEqualTo(templateEntity1.getInputSchema());
    assertThat(result.getOutputSchema()).isEqualTo(templateEntity1.getOutputSchema());
    assertThat(result.getConfig()).isEqualTo(templateEntity1.getConfig());
    assertThat(result.getCreatedAt()).isEqualTo(templateEntity1.getCreatedAt());
    assertThat(result.getUpdatedAt()).isEqualTo(templateEntity1.getUpdatedAt());
  }

  @Test
  void templateToDomain_shouldHandleNull() {
    // When
    TaskTemplate result = mapper.templateToDomain(null);

    // Then
    assertNull(result);
  }

  @Test
  void templateToDomain_shouldHandleNullFields() {
    // Given
    TaskTemplateEntity entity = new TaskTemplateEntity();
    entity.setId(UUID.randomUUID());
    entity.setName("test");
    entity.setVersion("v1.0");
    entity.setType(TaskType.HTTP_CALL);
    // Оставляем остальные поля null

    // When
    TaskTemplate result = mapper.templateToDomain(entity);

    // Then
    assertNotNull(result);
    assertThat(result.getId()).isEqualTo(entity.getId());
    assertThat(result.getName()).isEqualTo(entity.getName());
    assertThat(result.getInputSchema()).isNull();
    assertThat(result.getOutputSchema()).isNull();
    assertThat(result.getConfig()).isNull();
    assertThat(result.getCreatedAt()).isNull();
    assertThat(result.getUpdatedAt()).isNull();
  }

  @Test
  void dependencyToDomain_shouldMapParentAndChildIds() {
    // When
    TaskDependency result = mapper.dependencyToDomain(dependencyEntity);

    // Then
    assertNotNull(result);
    assertThat(result.getParentTaskId()).isEqualTo(templateEntity1.getId());
    assertThat(result.getChildTaskId()).isEqualTo(templateEntity2.getId());
    assertThat(result.getCondition()).isEqualTo(TaskCondition.SUCCESS);
  }

  @Test
  void dependencyToDomain_shouldHandleNullEntity() {
    // When
    TaskDependency result = mapper.dependencyToDomain(null);

    // Then
    assertNull(result);
  }

  @Test
  void dependencyToDomain_shouldHandleNullParentOrChild() {
    // Given
    TaskDependencyEntity entity = new TaskDependencyEntity();
    entity.setParent(null);
    entity.setChild(null);
    entity.setCondition(TaskCondition.ALWAYS);

    // When
    TaskDependency result = mapper.dependencyToDomain(entity);

    // Then
    assertNotNull(result);
    assertThat(result.getParentTaskId()).isNull();
    assertThat(result.getChildTaskId()).isNull();
    assertThat(result.getCondition()).isEqualTo(TaskCondition.ALWAYS);
  }

  @Test
  void toDomain_shouldMapBasicFields() {
    // When
    TaskGraph result = mapper.toDomain(graphEntity);

    // Then
    assertNotNull(result);
    assertThat(result.getId()).isEqualTo(graphEntity.getId());
    assertThat(result.getName()).isEqualTo(graphEntity.getName());
  }

  @Test
  void toDomain_shouldMapTemplates() {
    // When
    TaskGraph result = mapper.toDomain(graphEntity);

    // Then
    assertNotNull(result.getTasks());
    assertThat(result.getTasks()).hasSize(2);

    TaskTemplate template1 = result.getTasks().get(0);
    assertThat(template1.getId()).isEqualTo(templateEntity1.getId());
    assertThat(template1.getName()).isEqualTo(templateEntity1.getName());

    TaskTemplate template2 = result.getTasks().get(1);
    assertThat(template2.getId()).isEqualTo(templateEntity2.getId());
    assertThat(template2.getName()).isEqualTo(templateEntity2.getName());
  }

  @Test
  void toDomain_shouldMapDependencies() {
    // When
    TaskGraph result = mapper.toDomain(graphEntity);

    // Then
    assertNotNull(result.getDependencies());
    assertThat(result.getDependencies()).hasSize(1);

    TaskDependency dependency = result.getDependencies().get(0);
    assertThat(dependency.getParentTaskId()).isEqualTo(templateEntity1.getId());
    assertThat(dependency.getChildTaskId()).isEqualTo(templateEntity2.getId());
    assertThat(dependency.getCondition()).isEqualTo(TaskCondition.SUCCESS);
  }

  @Test
  void toDomain_shouldSetDefaultValuesForMissingFields() {
    // Given
    TaskGraphEntity entity = new TaskGraphEntity();
    entity.setName("Empty Graph");
    // Не устанавливаем templates и dependencies - они останутся пустые

    // When
    TaskGraph result = mapper.toDomain(entity);

    // Then
    assertNotNull(result);
    assertThat(result.getId()).isEqualTo(entity.getId());
    assertThat(result.getName()).isEqualTo(entity.getName());
    assertThat(result.getTasks()).isNotNull();
    assertThat(result.getDependencies()).isNotNull();
    assertThat(result.getEntryPointTaskId()).isNull();
    assertThat(result.isValidated()).isFalse();
  }

  @Test
  void toDomain_shouldHandleEmptyCollections() {
    // Given
    TaskGraphEntity entity = new TaskGraphEntity();
    entity.setName("Empty Graph");
    entity.setTemplates(new ArrayList<>());
    entity.setDependencies(new ArrayList<>());

    // When
    TaskGraph result = mapper.toDomain(entity);

    // Then
    assertNotNull(result);
    assertThat(result.getTasks()).isEmpty();
    assertThat(result.getDependencies()).isEmpty();
  }

  @Test
  void toDomain_shouldHandleNullEntity() {
    // When
    TaskGraph result = mapper.toDomain(null);

    // Then
    assertNull(result);
  }

  @Test
  void shouldAutomaticallyMapTemplateList() {
    // Given
    List<TaskTemplateEntity> entities = Arrays.asList(templateEntity1, templateEntity2);

    // When
    List<TaskTemplate> result = mapper.templateListToDomain(entities);

    // Then
    assertNotNull(result);
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getId()).isEqualTo(templateEntity1.getId());
    assertThat(result.get(1).getId()).isEqualTo(templateEntity2.getId());
  }

  @Test
  void shouldAutomaticallyMapDependencyList() {
    // Given
    List<TaskDependencyEntity> entities = Collections.singletonList(dependencyEntity);

    // When
    List<TaskDependency> result = mapper.dependencyListToDomain(entities);

    // Then
    assertNotNull(result);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getParentTaskId()).isEqualTo(templateEntity1.getId());
  }

  @Test
  void templateListToDomain_shouldHandleNullList() {
    // When
    List<TaskTemplate> result = mapper.templateListToDomain(null);

    // Then
    assertNull(result);
  }

  @Test
  void dependencyListToDomain_shouldHandleNullList() {
    // When
    List<TaskDependency> result = mapper.dependencyListToDomain(null);

    // Then
    assertNull(result);
  }

  @Test
  void testMapperInstanceIsNotNull() {
    assertNotNull(mapper, "Mapper should be initialized");
  }
}