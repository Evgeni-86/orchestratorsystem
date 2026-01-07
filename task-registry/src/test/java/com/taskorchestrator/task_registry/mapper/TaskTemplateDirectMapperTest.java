package com.taskorchestrator.task_registry.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.taskorchestrator.task_registry.dto.task.TaskTemplateResponseDto;
import com.taskorchestrator.task_registry.dto.task.TaskTemplateUpdateDto;
import com.taskorchestrator.task_registry.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry.enums.TaskType;
import com.taskorchestrator.task_registry.mapper.tasktemplate.TaskTemplateDirectMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskTemplateDirectMapperTest {

  private final TaskTemplateDirectMapper mapper = Mappers.getMapper(TaskTemplateDirectMapper.class);

  @Test
  void entityToResponseDto_shouldMapAllFieldsCorrectly() {
    // Given
    UUID id = UUID.randomUUID();
    Instant now = Instant.now();

    TaskTemplateEntity entity = new TaskTemplateEntity();
    entity.setId(id);
    entity.setName("test-name");
    entity.setVersion("v1.0");
    entity.setType(TaskType.HTTP_CALL);
    entity.setInputSchema(Map.of("field", "string"));
    entity.setOutputSchema(Map.of("result", "boolean"));
    entity.setConfig(Map.of("timeout", 5000));
    entity.setCreatedAt(now.minusSeconds(3600));
    entity.setUpdatedAt(now);

    // When
    TaskTemplateResponseDto dto = mapper.entityToResponseDto(entity);

    // Then
    assertThat(dto.id()).isEqualTo(id);
    assertThat(dto.name()).isEqualTo("test-name");
    assertThat(dto.version()).isEqualTo("v1.0");
    assertThat(dto.type()).isEqualTo(TaskType.HTTP_CALL);
    assertThat(dto.inputSchema()).containsEntry("field", "string");
    assertThat(dto.outputSchema()).containsEntry("result", "boolean");
    assertThat(dto.config()).containsEntry("timeout", 5000);
    assertThat(dto.createdAt()).isEqualTo(now.minusSeconds(3600));
    assertThat(dto.updatedAt()).isEqualTo(now);
  }

  @Test
  void updateEntityFromDto_shouldUpdateOnlyNonNullFields() {
    // Given
    UUID id = UUID.randomUUID();
    Instant createdAt = Instant.now().minusSeconds(3600);

    TaskTemplateEntity entity = new TaskTemplateEntity();
    entity.setId(id);
    entity.setName("old-name");
    entity.setVersion("v1.0");
    entity.setType(TaskType.SCRIPT);
    entity.setInputSchema(new HashMap<>(Map.of("old", "value")));
    entity.setCreatedAt(createdAt);

    TaskTemplateUpdateDto dto = new TaskTemplateUpdateDto(
        "new-name",  // обновляем
        null,        // не обновляем (должно остаться "v1.0")
        TaskType.HTTP_CALL, // обновляем
        Map.of("new", "schema"), // обновляем
        null,        // не обновляем
        Map.of()     // обновляем на пустую мапу
    );

    // When
    mapper.updateEntityFromDto(entity, dto);

    // Then
    assertThat(entity.getId()).isEqualTo(id); // не изменился
    assertThat(entity.getName()).isEqualTo("new-name");
    assertThat(entity.getVersion()).isEqualTo("v1.0"); // старое значение
    assertThat(entity.getType()).isEqualTo(TaskType.HTTP_CALL);
    assertThat(entity.getInputSchema()).containsEntry("new", "schema");
    assertThat(entity.getOutputSchema()).isNull(); // осталось null
    assertThat(entity.getConfig()).isEmpty(); // обновилось на пустую мапу
    assertThat(entity.getCreatedAt()).isEqualTo(createdAt); // не изменилось
  }

  @Test
  void updateEntityFromDto_shouldIgnoreNullMaps() {
    // Given
    TaskTemplateEntity entity = new TaskTemplateEntity();
    entity.setInputSchema(Map.of("keep", "this"));
    entity.setOutputSchema(Map.of("keep", "this"));
    entity.setConfig(Map.of("keep", "this"));

    TaskTemplateUpdateDto dto = new TaskTemplateUpdateDto(
        null, null, null, null, null, null
    );

    // When
    mapper.updateEntityFromDto(entity, dto);

    // Then
    assertThat(entity.getInputSchema()).containsEntry("keep", "this");
    assertThat(entity.getOutputSchema()).containsEntry("keep", "this");
    assertThat(entity.getConfig()).containsEntry("keep", "this");
  }
}
