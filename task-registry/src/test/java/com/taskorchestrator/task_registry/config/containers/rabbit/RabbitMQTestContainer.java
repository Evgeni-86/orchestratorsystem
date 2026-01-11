package com.taskorchestrator.task_registry.config.containers.rabbit;

import org.testcontainers.containers.RabbitMQContainer;

public class RabbitMQTestContainer implements RabbitTestContainer {

  private RabbitMQContainer rabbitMQContainer;

  @Override
  public void afterPropertiesSet() {
    if (null == rabbitMQContainer) {
      rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.11.8-management")
          .withExposedPorts(5672, 15672)
          .withReuse(true);

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        if (rabbitMQContainer != null && rabbitMQContainer.isRunning()) {
          rabbitMQContainer.stop();
        }
      }));
    }
    if (!rabbitMQContainer.isRunning()) {
      rabbitMQContainer.start();
    }
  }

  @Override
  public RabbitMQContainer getTestContainer() {
    return rabbitMQContainer;
  }
}
