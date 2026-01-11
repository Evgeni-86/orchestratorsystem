package com.taskorchestrator.task_registry.domain;

import com.taskorchestrator.task_registry.enums.OutboxStatus;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
  private String type;
  private String payload;
  private OutboxStatus outboxStatus;
}
