package com.taskorchestrator.task_registry_gex.infrastructure.exception;

public class GraphDomainException extends RuntimeException {

  public GraphDomainException(String message) {
    super(message);
  }

  public GraphDomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
