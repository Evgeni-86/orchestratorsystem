package com.taskorchestrator.task_registry_gex.application.core.domain.enums;

public enum OutboxStatus {
  PENDING,    // Сообщение создано, ожидает обработки
  STARTED,    // Взято в обработку scheduler'ом
  COMPLETED,  // Успешно отправлено
  FAILED      // Ошибка отправки
}