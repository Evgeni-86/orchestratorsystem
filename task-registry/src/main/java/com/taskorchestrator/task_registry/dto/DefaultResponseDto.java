package com.taskorchestrator.task_registry.dto;

import com.taskorchestrator.task_registry.dto.meta.MetaResponseDto;

public interface DefaultResponseDto<T> {

  T data();

  MetaResponseDto meta();
}
