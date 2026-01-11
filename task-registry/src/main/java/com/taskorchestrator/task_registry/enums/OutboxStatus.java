package com.taskorchestrator.task_registry.enums;

public enum OutboxStatus {
  PENDING,    // Сообщение создано, ожидает обработки
  STARTED,    // Взято в обработку scheduler'ом
  COMPLETED,  // Успешно отправлено
  FAILED      // Ошибка отправки
}