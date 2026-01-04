package com.taskorchestrator.task_registry.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "task_graphs")
@Entity
public class TaskGraphEntity {

  @Id
  @UuidGenerator
  @Column(name = "id", updatable = false)
  @Setter(AccessLevel.NONE)
  private UUID id;

  @Column(name = "name", nullable = false)
  private String name;

  @OneToMany(mappedBy = "graph", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TaskDependencyEntity> dependencies = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "graph_tasks",
      joinColumns = @JoinColumn(name = "graph_id"),
      inverseJoinColumns = @JoinColumn(name = "template_id")
  )
  private List<TaskTemplateEntity> templates = new ArrayList<>();

  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @PrePersist
  protected void updateTimestamps() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
