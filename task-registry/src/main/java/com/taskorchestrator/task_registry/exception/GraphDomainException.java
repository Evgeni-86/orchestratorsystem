package com.taskorchestrator.task_registry.exception;

public class GraphDomainException extends RuntimeException {

  public GraphDomainException(String message) {
    super(message);
  }

  public GraphDomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
