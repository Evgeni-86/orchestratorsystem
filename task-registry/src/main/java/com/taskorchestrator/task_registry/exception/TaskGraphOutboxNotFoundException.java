package com.taskorchestrator.task_registry.exception;

public class TaskGraphOutboxNotFoundException extends RuntimeException {

  public TaskGraphOutboxNotFoundException(String message) {
    super(message);
  }
}
