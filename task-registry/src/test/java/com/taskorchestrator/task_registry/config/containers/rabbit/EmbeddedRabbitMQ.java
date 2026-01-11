package com.taskorchestrator.task_registry.config.containers.rabbit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EmbeddedRabbitMQ {

  Class<? extends RabbitMQTestContainer> container();
}
