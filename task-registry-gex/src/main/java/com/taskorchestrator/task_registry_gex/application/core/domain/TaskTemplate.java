package com.taskorchestrator.task_registry_gex.application.core.domain;

import com.taskorchestrator.task_registry_gex.application.core.domain.enums.TaskType;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class TaskTemplate {

  private UUID id;             // UUID
  private String name;               // "process-payment"
  private String version;            // "v1.0" (пока простой, позже добавим версионность)
  private TaskType type;             // HTTP_CALL, SCRIPT, DATABASE_QUERY
  private Map<String, Object> inputSchema;  // JSON Schema для валидации входных данных
  private Map<String, Object> outputSchema; // JSON Schema для выходных данных
  private Map<String, Object> config;       // Конфигурация выполнения (таймауты, заголовки и т.д.)
  private Instant createdAt;
  private Instant updatedAt;
}
