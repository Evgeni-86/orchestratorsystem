package com.taskorchestrator.task_registry.dto;

import com.taskorchestrator.task_registry.dto.meta.MetaResponseDto;

public record ResponseDto<T>(
    T data,
    MetaResponseDto meta
) implements DefaultResponseDto<T> {

}
