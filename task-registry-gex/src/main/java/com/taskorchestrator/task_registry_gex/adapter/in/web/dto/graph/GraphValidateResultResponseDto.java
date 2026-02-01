package com.taskorchestrator.task_registry_gex.adapter.in.web.dto.graph;

import java.util.List;

public record GraphValidateResultResponseDto(
    boolean isValid,
    List<String> errors,
    List<String> warnings) {

}
