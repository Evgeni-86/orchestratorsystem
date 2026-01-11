package com.taskorchestrator.task_registry.config;

import com.taskorchestrator.task_registry.config.containers.db.EmbeddedSQL;
import com.taskorchestrator.task_registry.config.containers.db.PostgreSqlTestContainer;
import com.taskorchestrator.task_registry.config.containers.rabbit.EmbeddedRabbitMQ;
import com.taskorchestrator.task_registry.config.containers.rabbit.RabbitMQTestContainer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EmbeddedSQL(container = PostgreSqlTestContainer.class)
@EmbeddedRabbitMQ(container = RabbitMQTestContainer.class)
public @interface IntegrationTest {
}
