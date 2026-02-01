package com.taskorchestrator.task_registry_gex.application.core.port.out;

public interface OutboxScheduler {

  void processOutboxMessage();
}
