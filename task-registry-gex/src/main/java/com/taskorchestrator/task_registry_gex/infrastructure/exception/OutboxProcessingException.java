package com.taskorchestrator.task_registry_gex.infrastructure.exception;

public class OutboxProcessingException extends RuntimeException {

  public OutboxProcessingException(String message) {
    super(message);
  }

  public OutboxProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
