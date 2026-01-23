package com.taskorchestrator.task_registry.entity;

import com.taskorchestrator.task_registry.domain.TaskGraphEventPayload;
import com.taskorchestrator.task_registry.enums.OutboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "task_graph_outbox")
@Entity
public class TaskGraphOutboxEntity {

  @Id
  @UuidGenerator
  @Column(name = "id", updatable = false)
  @Setter(AccessLevel.NONE)
  private UUID id;

  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @Column(name = "processed_at")
  private Instant processedAt;

  @Column(name = "type", nullable = false)
  private String type;

  @Column(name = "payload", nullable = false)
  @JdbcTypeCode(SqlTypes.JSON)
  private TaskGraphEventPayload payload;

  @Column(name = "outbox_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private OutboxStatus outboxStatus;

  @Version
  private Integer version;

  @PrePersist
  protected void updateTimestamps() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
