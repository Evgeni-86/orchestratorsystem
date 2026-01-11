package com.taskorchestrator.task_registry.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskorchestrator.task_registry.domain.TaskGraphEventPayload;
import com.taskorchestrator.task_registry.domain.TaskGraphOutboxMessage;
import com.taskorchestrator.task_registry.enums.OutboxStatus;
import com.taskorchestrator.task_registry.exception.GraphDomainException;
import com.taskorchestrator.task_registry.service.OutboxService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskGraphOutboxHelper {

  public static final String GRAPH_PROCESSING = "GraphProcessing";
  private final OutboxService outboxService;
  private final ObjectMapper objectMapper;

  public List<TaskGraphOutboxMessage> getTaskGraphOutboxMessageByOutboxMessageStatus(
      OutboxStatus outboxStatus) {
    return outboxService.findByTypeAndOutboxStatus(GRAPH_PROCESSING, outboxStatus);
  }

  public TaskGraphOutboxMessage getTaskGraphOutboxMessage() {
    return outboxService.findByType(GRAPH_PROCESSING);
  }

  public void save(TaskGraphOutboxMessage taskGraphOutboxMessage) {
    var response = outboxService.save(taskGraphOutboxMessage);
    if (Objects.isNull(response)) {
      throw new GraphDomainException("Failed to save outbox message id : " +
          taskGraphOutboxMessage.getId());
    }
    log.info("Outbox message id : {} saved successfully", response.getId());
  }

  public void saveTaskGraphOutboxMessage(TaskGraphEventPayload payload, OutboxStatus outboxStatus) {
    save(TaskGraphOutboxMessage.builder()
        .id(UUID.randomUUID())
        .createdAt(payload.getCreatedAt())
        .type(GRAPH_PROCESSING)
        .payload(createPayload(payload))
        .outboxStatus(outboxStatus)
        .build());
  }

  private String createPayload(TaskGraphEventPayload payload) {
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      log.error("Failed to create payload for outbox message", e);
      throw new GraphDomainException("Failed to create payload for outbox message");
    }
  }

  public void deleteTaskGraphOutboxMessageByOutboxStatus(OutboxStatus outboxStatus) {
    outboxService.deleteByTypeAndOutboxStatus(
        GRAPH_PROCESSING,
        outboxStatus);
  }
}
