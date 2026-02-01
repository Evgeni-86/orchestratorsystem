package com.taskorchestrator.task_registry_gex.adapter.in.web.controller;

import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.ResponseDto;
import com.taskorchestrator.task_registry_gex.adapter.in.web.util.ResponseBuilder;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
public abstract class BaseController {

  protected final ResponseBuilder responseBuilder;

  protected <T> ResponseEntity<ResponseDto<T>> createResponse(T data, Object pathVariable) {
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(pathVariable)
        .toUri();
    return ResponseEntity.created(location).body(responseBuilder.buildSingle(data));
  }

  protected <T> ResponseEntity<ResponseDto<T>> okResponse(T data) {
    return ResponseEntity.ok(responseBuilder.buildSingle(data));
  }

  protected <T> ResponseEntity<ResponseDto<List<T>>> okResponse(Page<T> page) {
    return ResponseEntity.ok(responseBuilder.buildPaged(page));
  }

  protected ResponseEntity<Void> noContentResponse() {
    return ResponseEntity.noContent().build();
  }
}
