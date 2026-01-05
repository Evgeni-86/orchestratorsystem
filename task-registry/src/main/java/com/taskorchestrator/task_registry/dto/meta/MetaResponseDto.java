package com.taskorchestrator.task_registry.dto.meta;

public record MetaResponseDto(
    boolean firstPage,
    boolean LastPage,
    Integer numberOfElements,
    Integer pageSize,
    Integer totalPages
) {

}
