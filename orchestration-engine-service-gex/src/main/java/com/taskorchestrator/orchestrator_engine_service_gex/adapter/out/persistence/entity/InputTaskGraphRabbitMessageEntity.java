package com.taskorchestrator.orchestrator_engine_service_gex.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "input_message_rabbitmq")
@Entity
public class InputTaskGraphRabbitMessageEntity {

  @Id
  @UuidGenerator
  @Column(name = "id", updatable = false)
  private UUID id;

  @Column(name = "graph_id", nullable = false)
  private String graphId;

  //Время создания графа!!!
  @Column(name = "created_at", updatable = false)
  private Instant createdAt;
}
