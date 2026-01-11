package com.taskorchestrator.task_registry.config.containers.rabbit;

import org.springframework.beans.factory.InitializingBean;
import org.testcontainers.containers.RabbitMQContainer;

public interface RabbitTestContainer extends InitializingBean {

  RabbitMQContainer getTestContainer();
}
