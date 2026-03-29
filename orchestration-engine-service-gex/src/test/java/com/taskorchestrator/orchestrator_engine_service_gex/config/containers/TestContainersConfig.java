package com.taskorchestrator.orchestrator_engine_service_gex.config.containers;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;

@Configuration
public class TestContainersConfig {

  @Bean
  @ServiceConnection
  RabbitMQContainer rabbitMQContainer() {
    return new RabbitMQContainer("rabbitmq:3.11.8-management")
        .withUser("rabbit-user-prod", "rabbit-pwd-prod")
        .withExposedPorts(5672, 15672)
        .withCreateContainerCmdModifier(cmd ->
            cmd.withHostConfig(
                new HostConfig().withPortBindings(
                    new PortBinding(Ports.Binding.bindPort(5672), new ExposedPort(5672)),
                    new PortBinding(Ports.Binding.bindPort(15672), new ExposedPort(15672))
                )
            )
        );
  }

  @Bean
  @ServiceConnection
  PostgreSQLContainer<?> postgreSQLContainer() {
    return new PostgreSQLContainer<>("postgres:17-alpine")
        .withUsername("postgresdb-user-prod")
        .withPassword("postgresdb-pwd-prod")
        .withExposedPorts(5432)
        .withCreateContainerCmdModifier(cmd ->
            cmd.withHostConfig(
                new HostConfig().withPortBindings(
                    new PortBinding(Ports.Binding.bindPort(5432), new ExposedPort(5432))
                )
            )
        );
  }
}
