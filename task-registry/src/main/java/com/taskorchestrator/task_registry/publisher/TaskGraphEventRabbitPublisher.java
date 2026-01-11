package com.taskorchestrator.task_registry.publisher;

import com.taskorchestrator.task_registry.domain.TaskGraphEventPayload;
import com.taskorchestrator.task_registry.domain.TaskGraphOutboxMessage;
import com.taskorchestrator.task_registry.enums.OutboxStatus;
import com.taskorchestrator.task_registry.rabbitmq.RabbitMQMessageHelper;
import java.util.UUID;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskGraphEventRabbitPublisher implements TaskGraphRequestMessagePublisher {

  private final RabbitTemplate rabbitTemplate;
  private final RabbitMQMessageHelper rabbitMQMessageHelper;

  @Value("${rabbitmq.exchange.task-graph}")
  private String exchangeName;
  @Value("${rabbitmq.routing-key.task-graph}")
  private String routingKey;

  @Override
  public void publish(TaskGraphOutboxMessage taskGraphOutboxMessage,
      BiConsumer<TaskGraphOutboxMessage, OutboxStatus> outboxCallback) {

    log.info("Received TaskGraphOutboxMessage for id: {}", taskGraphOutboxMessage.getId());

    try {
      TaskGraphEventPayload taskGraphEventPayload =
          rabbitMQMessageHelper.getEventPayload(taskGraphOutboxMessage.getPayload(),
              TaskGraphEventPayload.class);

      String messageId = UUID.randomUUID().toString();

      CorrelationData rabbitMQCallback = rabbitMQMessageHelper.getRabbitMQCallback(
          exchangeName,
          taskGraphEventPayload,
          taskGraphOutboxMessage,
          outboxCallback,
          messageId,
          taskGraphEventPayload.getClass().getSimpleName());

      rabbitTemplate.convertAndSend(
          exchangeName,
          routingKey,
          taskGraphEventPayload,
          rabbitMQCallback
      );

      log.info("TaskGraphEventPayload sent to rabbitmq for graph id: {}",
          taskGraphEventPayload.getGraphId());
    } catch (Exception e) {
      log.error("Error while sending TaskGraphOutboxMessage" +
              " to rabbitmq with id: {}, error: {}",
          taskGraphOutboxMessage.getId(), e.getMessage());
    }
  }
}
