package com.taskorchestrator.orchestrator_engine_service_gex.config.containers;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Bean
  public Queue taskGraphQueue() {
    return new Queue("task_graph_queue", true);
  }
}
