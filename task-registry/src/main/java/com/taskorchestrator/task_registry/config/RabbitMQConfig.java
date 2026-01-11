package com.taskorchestrator.task_registry.config;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.exchange.task-graph}")
  private String taskGraphExchange;

  @Bean
  public Declarables declarables() {
    return new Declarables(
        // 1. Обменник
        ExchangeBuilder.directExchange(taskGraphExchange)
            .durable(true)
            .build(),

        // 2. Очередь
        QueueBuilder.durable("task_graph_queue")
            .withArgument("x-dead-letter-exchange", taskGraphExchange + ".dlx")
            .withArgument("x-dead-letter-routing-key", "task_graph.dead")
            .build(),

        // 3. Привязка очереди к обменнику
        BindingBuilder.bind(new Queue("task_graph_queue"))
            .to(new DirectExchange(taskGraphExchange))
            .with("task_graph.routing.key")
    );
  }
}
