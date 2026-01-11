package com.taskorchestrator.task_registry.config.containers.db;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreSqlTestContainer implements SqlTestContainer {

  private PostgreSQLContainer<?> postgreSQLContainer;

  @Override
  public void afterPropertiesSet() {
    if (null == postgreSQLContainer) {
      postgreSQLContainer = new PostgreSQLContainer<>("postgres:17-alpine")
          .withDatabaseName("sampleApplication")
          .withReuse(true);

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
          postgreSQLContainer.stop();
        }
      }));
    }
    if (!postgreSQLContainer.isRunning()) {
      postgreSQLContainer.start();
    }
  }

  @Override
  public JdbcDatabaseContainer<?> getTestContainer() {
    return postgreSQLContainer;
  }
}
