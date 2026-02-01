package com.taskorchestrator.task_registry_gex.infrastructure.config;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.exchange.task-graph}")
  private String taskGraphExchange;
  @Value("${rabbitmq.routing-key.task-graph}")
  private String taskGraphRoutingKey;
  @Value("${rabbitmq.queue.task-graph}")
  private String taskGraphQueue;

  @Bean
  public Declarables declarables() {
    DirectExchange exchange = ExchangeBuilder.directExchange(taskGraphExchange)
        .durable(true)
        .build();

    Queue queue = QueueBuilder.durable(taskGraphQueue)
        .withArgument("x-dead-letter-exchange", taskGraphExchange + ".dlx")
        .withArgument("x-dead-letter-routing-key", taskGraphRoutingKey + ".dead")
        .build();

    return new Declarables(
        exchange,
        queue,
        BindingBuilder.bind(queue)
            .to(exchange)
            .with(taskGraphRoutingKey));
  }

  @Bean
  public Declarables deadLetterDeclarables() {
    String dlxExchange = taskGraphExchange + ".dlx";
    String dlxQueue = taskGraphQueue + ".dlq";
    String dlxRoutingKey = taskGraphRoutingKey + ".dead";

    Queue dlq = QueueBuilder.durable(dlxQueue).build();
    DirectExchange dlx = ExchangeBuilder.directExchange(dlxExchange).durable(true).build();

    return new Declarables(
        dlx,
        dlq,
        BindingBuilder.bind(dlq)
            .to(dlx)
            .with(dlxRoutingKey));
  }

  @Bean
  public Jackson2JsonMessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
