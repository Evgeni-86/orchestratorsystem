package com.taskorchestrator.task_registry_gex.application.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TaskGraphEventPayload {

  @JsonProperty
  private String graphId;
  @JsonProperty
  private String createdAt;
}
