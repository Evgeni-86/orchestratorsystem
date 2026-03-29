package com.taskorchestrator.orchestrator_engine_service_gex.adapter.in.rabbit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
@AllArgsConstructor
public class TaskGraphEventPayloadDto {

  @JsonProperty
  private String graphId;
  @JsonProperty
  private String createdAt;
}
