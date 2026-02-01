package com.taskorchestrator.task_registry_gex.adapter.in.web.dto;

import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.meta.MetaResponseDto;

public record ResponseDto<T>(
    T data,
    MetaResponseDto meta
) implements DefaultResponseDto<T> {

}
