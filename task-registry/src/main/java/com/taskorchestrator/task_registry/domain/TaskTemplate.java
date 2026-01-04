package com.taskorchestrator.task_registry.domain;

import com.taskorchestrator.task_registry.enums.TaskType;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class TaskTemplate {

  private final String id;                 // UUID
  private String name;               // "process-payment"
  private String version;            // "v1.0" (пока простой, позже добавим версионность)
  private TaskType type;             // HTTP_CALL, SCRIPT, DATABASE_QUERY
  private Map<String, Object> inputSchema;  // JSON Schema для валидации входных данных
  private Map<String, Object> outputSchema; // JSON Schema для выходных данных
  private Map<String, Object> config;       // Конфигурация выполнения (таймауты, заголовки и т.д.)
  private final Instant createdAt;
  private Instant updatedAt;
}
