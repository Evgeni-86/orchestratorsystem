package com.taskorchestrator.task_registry.config.containers.db;

import org.springframework.beans.factory.InitializingBean;
import org.testcontainers.containers.JdbcDatabaseContainer;

public interface SqlTestContainer extends InitializingBean {
  JdbcDatabaseContainer<?> getTestContainer();
}