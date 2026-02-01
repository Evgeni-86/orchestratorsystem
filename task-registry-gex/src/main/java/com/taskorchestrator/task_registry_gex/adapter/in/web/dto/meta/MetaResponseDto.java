package com.taskorchestrator.task_registry_gex.adapter.in.web.dto.meta;

public record MetaResponseDto(
    boolean firstPage,
    boolean lastPage,
    Integer numberOfElements,
    Integer pageNumber,
    Integer pageSize,
    Integer totalPages
) {

}
