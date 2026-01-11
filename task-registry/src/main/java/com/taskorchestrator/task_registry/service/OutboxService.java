package com.taskorchestrator.task_registry.service;

import com.taskorchestrator.task_registry.domain.TaskGraphOutboxMessage;
import com.taskorchestrator.task_registry.entity.TaskGraphOutboxEntity;
import com.taskorchestrator.task_registry.enums.OutboxStatus;
import com.taskorchestrator.task_registry.exception.ObjectNotFoundException;
import com.taskorchestrator.task_registry.mapper.outbox.TaskGraphOutboxEntityMapper;
import com.taskorchestrator.task_registry.repository.TaskGraphOutboxJpaRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

  private final TaskGraphOutboxJpaRepository taskGraphOutboxJpaRepository;
  private final TaskGraphOutboxEntityMapper taskGraphOutboxEntityMapper;

  @Transactional
  public TaskGraphOutboxMessage save(TaskGraphOutboxMessage taskGraphOutboxMessage) {
    TaskGraphOutboxEntity entity = taskGraphOutboxJpaRepository.save(
        taskGraphOutboxEntityMapper
            .toTaskGraphOutboxEntity(taskGraphOutboxMessage));
    return taskGraphOutboxEntityMapper
        .toTaskGraphOutboxMessage(entity);
  }

  @Transactional(readOnly = true)
  public List<TaskGraphOutboxMessage> findByTypeAndOutboxStatus(
      String type, OutboxStatus outboxStatus) {
    return taskGraphOutboxJpaRepository
        .findByTypeAndOutboxStatus(type, outboxStatus)
        .map(list -> list.stream()
            .map(taskGraphOutboxEntityMapper::toTaskGraphOutboxMessage)
            .toList())
        .orElse(Collections.emptyList());
  }

  @Transactional(readOnly = true)
  public TaskGraphOutboxMessage findByType(String type) {
    return taskGraphOutboxJpaRepository
        .findByType(type)
        .map(taskGraphOutboxEntityMapper::toTaskGraphOutboxMessage)
        .orElseThrow(() -> new ObjectNotFoundException(
            "Task graph outbox message not found with {type}: " + type));
  }

  @Transactional
  public void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
    taskGraphOutboxJpaRepository.deleteByTypeAndOutboxStatus(type, outboxStatus);
  }
}
