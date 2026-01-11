package com.taskorchestrator.task_registry.service;

import com.taskorchestrator.task_registry.domain.TaskGraphOutboxMessage;
import com.taskorchestrator.task_registry.entity.TaskGraphOutboxEntity;
import com.taskorchestrator.task_registry.enums.OutboxStatus;
import com.taskorchestrator.task_registry.exception.ObjectNotFoundException;
import com.taskorchestrator.task_registry.mapper.outbox.TaskGraphOutboxEntityMapper;
import com.taskorchestrator.task_registry.repository.TaskGraphOutboxJpaRepository;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

  public static final String GRAPH_PROCESSING = "GraphProcessing";
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

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateStatusIfStarted(UUID messageId, OutboxStatus newStatus) {
    TaskGraphOutboxEntity entity = taskGraphOutboxJpaRepository.findById(messageId)
        .orElseThrow(() -> new ObjectNotFoundException("Message not found: " + messageId));
    if (entity.getOutboxStatus() == OutboxStatus.STARTED) {
      entity.setOutboxStatus(newStatus);
      entity.setProcessedAt(Instant.now());
    } else {
      log.debug("Message {} already processed (status: {})", messageId, entity.getOutboxStatus());
    }
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
  public List<TaskGraphOutboxMessage> findByType(String type) {
    return taskGraphOutboxJpaRepository
        .findByType(type)
        .map(list -> list.stream()
            .map(taskGraphOutboxEntityMapper::toTaskGraphOutboxMessage)
            .toList())
        .orElse(Collections.emptyList());
  }

  @Transactional
  public void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
    taskGraphOutboxJpaRepository.deleteByTypeAndOutboxStatus(type, outboxStatus);
  }

  @Transactional
  public List<TaskGraphOutboxMessage> reservePendingMessages(int limit) {
    Pageable pageable = PageRequest.of(0, limit);
    List<TaskGraphOutboxEntity> entities = taskGraphOutboxJpaRepository
        .findByOutboxStatusWithLock(OutboxStatus.PENDING, pageable);
    if (entities.isEmpty()) {
      return Collections.emptyList();
    }
    entities.forEach(e -> {
      e.setOutboxStatus(OutboxStatus.STARTED);
      e.setProcessedAt(Instant.now());
    });
    List<TaskGraphOutboxEntity> savedEntities = taskGraphOutboxJpaRepository.saveAll(entities);
    return taskGraphOutboxEntityMapper.toTaskGraphOutboxMessagesList(savedEntities);
  }
}
