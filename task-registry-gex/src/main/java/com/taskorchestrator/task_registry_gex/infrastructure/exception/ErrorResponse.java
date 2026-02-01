package com.taskorchestrator.task_registry_gex.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

  private final int status;
  private final String message;
  private String stackTrace;
  private List<ValidationError> errors;

  private record ValidationError(String field, String message) {

  }

  public void addValidationError(String field, String errorMessage) {
    if (Objects.isNull(errors)) {
      this.errors = new ArrayList<>();
    }

    this.errors.add(new ValidationError(field, errorMessage));
  }
}
