package com.taskorchestrator.task_registry.entity;

import com.taskorchestrator.task_registry.enums.TaskCondition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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
@Table(name = "task_dependencies")
@Entity
public class TaskDependencyEntity {

  @Id
  @UuidGenerator
  @Column(name = "id", updatable = false)
  @Setter(AccessLevel.NONE)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "graph_id", nullable = false)
  private TaskGraphEntity graph;

  @ManyToOne
  @JoinColumn(name = "parent_template_id", nullable = false)
  private TaskTemplateEntity parent;

  @ManyToOne
  @JoinColumn(name = "child_template_id", nullable = false)
  private TaskTemplateEntity child;

  @Column(name = "condition", nullable = false, length = 50)
  @Enumerated(EnumType.STRING)
  private TaskCondition condition;

  @PrePersist
  @PreUpdate
  private void validate() {
    if (parent != null && child != null && parent.equals(child)) {
      throw new IllegalArgumentException("Task cannot depend on itself");
    }
  }
}
