package com.taskorchestrator.task_registry.scheduler;

public interface OutboxScheduler {

  void processOutboxMessage();
}
