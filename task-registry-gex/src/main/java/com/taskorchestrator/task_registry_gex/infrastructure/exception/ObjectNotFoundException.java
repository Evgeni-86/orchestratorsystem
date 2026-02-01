package com.taskorchestrator.task_registry_gex.infrastructure.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("java:S110")
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ObjectNotFoundException extends EntityNotFoundException {

  public ObjectNotFoundException(String message) {
    super(message);
  }
}
