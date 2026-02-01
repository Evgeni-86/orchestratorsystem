package com.taskorchestrator.task_registry_gex.application.core.domain;

import static com.taskorchestrator.task_registry_gex.application.core.service.OutboxServiceImpl.GRAPH_PROCESSING;

import com.taskorchestrator.task_registry_gex.application.core.domain.enums.OutboxStatus;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class TaskGraphOutboxMessage {

  private UUID id;
  private Instant createdAt;
  private Instant processedAt;
  @Default
  private String type = GRAPH_PROCESSING;
  private TaskGraphEventPayload payload;
  private OutboxStatus outboxStatus;
}
