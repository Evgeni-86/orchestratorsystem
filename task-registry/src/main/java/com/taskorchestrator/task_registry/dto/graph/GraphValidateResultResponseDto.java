package com.taskorchestrator.task_registry.dto.graph;

import java.util.List;

public record GraphValidateResultResponseDto(
    boolean isValid,
    List<String> errors,
    List<String> warnings) {

}
