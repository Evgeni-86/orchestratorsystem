package com.taskorchestrator.task_registry_gex.infrastructure.exception;

public class TaskGraphOutboxNotFoundException extends RuntimeException {

  public TaskGraphOutboxNotFoundException(String message) {
    super(message);
  }
}
