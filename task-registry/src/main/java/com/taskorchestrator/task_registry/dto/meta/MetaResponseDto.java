package com.taskorchestrator.task_registry.dto.meta;

public record MetaResponseDto(
    boolean firstPage,
    boolean lastPage,
    Integer numberOfElements,
    Integer pageNumber,
    Integer pageSize,
    Integer totalPages
) {

}
