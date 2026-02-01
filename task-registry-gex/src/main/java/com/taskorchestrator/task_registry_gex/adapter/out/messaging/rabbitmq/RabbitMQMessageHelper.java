package com.taskorchestrator.task_registry_gex.adapter.out.messaging.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskorchestrator.task_registry_gex.application.core.domain.enums.OutboxStatus;
import com.taskorchestrator.task_registry_gex.infrastructure.exception.GraphDomainException;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMQMessageHelper {

  private final ObjectMapper objectMapper;

  public <T, U> CorrelationData getRabbitMQCallback(
      String exchangeName,
      T messagePayload,
      U outboxMessage,
      BiConsumer<U, OutboxStatus> outboxCallback,
      String messageId,
      String modelName) {

    CorrelationData correlationData = new CorrelationData(messageId);

    // Обработчик результата отправки (аналог ListenableFutureCallback)
    correlationData.getFuture().whenComplete((confirm, throwable) -> {
      if (throwable != null) {
        // Ошибка при отправке (аналог onFailure)
        handleFailure(exchangeName, messagePayload, outboxMessage,
            outboxCallback, messageId, modelName, throwable);
      } else if (confirm != null && confirm.isAck()) {
        // Успешная отправка (аналог onSuccess)
        handleSuccess(exchangeName, outboxMessage, outboxCallback, messageId);
      } else {
        // Брокер не подтвердил (NACK)
        handleFailure(exchangeName, messagePayload, outboxMessage,
            outboxCallback, messageId, modelName,
            new Exception("Broker rejected message"));
      }
    });

    return correlationData;
  }

  private <T, U> void handleFailure(String exchangeName, T messagePayload,
      U outboxMessage,
      BiConsumer<U, OutboxStatus> outboxCallback,
      String messageId, String modelName,
      Throwable ex) {
    log.error("Error while sending {} with message: {} to exchange: {}. Message ID: {}",
        modelName,
        messagePayload != null ? messagePayload.toString() : "null",
        exchangeName,
        messageId,
        ex);

    outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
  }

  private <U> void handleSuccess(String exchangeName, U outboxMessage,
      BiConsumer<U, OutboxStatus> outboxCallback,
      String messageId) {
    log.info("Message successfully sent to exchange: {}. Message ID: {}",
        exchangeName, messageId);

    outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
  }

  /**
   * Настраивает обработку возвращенных сообщений (дополнительно к основному callback, специфика
   * RabbitMQ)
   */
  public <U> void setupReturnsCallback(
      RabbitTemplate rabbitTemplate,
      U outboxMessage,
      BiConsumer<U, OutboxStatus> outboxCallback) {

    rabbitTemplate.setReturnsCallback(returned -> {
      log.error("Message returned - could not be delivered. " +
              "Exchange: {}, Routing Key: {}, Reply Code: {}",
          returned.getExchange(),
          returned.getRoutingKey(),
          returned.getReplyCode());

      outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
    });
  }

  /**
   * Парсит JSON
   */
  public <T> T getEventPayload(String payload, Class<T> outputType) {
    try {
      return objectMapper.readValue(payload, outputType);
    } catch (JsonProcessingException e) {
      log.error("Could not read {} object!", outputType.getName(), e);
      throw new GraphDomainException("Could not read " + outputType.getName() + " object!", e);
    }
  }
}
