package com.taskorchestrator.task_registry.util;

import com.taskorchestrator.task_registry.dto.ResponseDto;
import com.taskorchestrator.task_registry.dto.meta.MetaResponseDto;
import com.taskorchestrator.task_registry.mapper.MetaMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseBuilder {

  private final MetaMapper metaMapper;

  public <T> ResponseDto<T> buildSingle(T data) {
    return new ResponseDto<>(data, null);
  }

  public <T> ResponseDto<List<T>> buildPaged(Page<T> page) {
    MetaResponseDto meta = metaMapper.toDto(page);
    return new ResponseDto<>(page.getContent(), meta);
  }
}
