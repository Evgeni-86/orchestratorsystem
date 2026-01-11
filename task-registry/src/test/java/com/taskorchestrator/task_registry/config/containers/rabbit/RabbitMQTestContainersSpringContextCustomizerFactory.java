package com.taskorchestrator.task_registry.config.containers.rabbit;

import java.util.List;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;

public class RabbitMQTestContainersSpringContextCustomizerFactory implements
    ContextCustomizerFactory {

  private static RabbitTestContainer prodTestContainer;

  @Override
  public ContextCustomizer createContextCustomizer(Class<?> testClass,
      List<ContextConfigurationAttributes> configAttributes) {
    System.out.println("=== ContextCustomizerFactory called for: " + testClass.getName());
    return new ContextCustomizer() {
      @Override
      public void customizeContext(ConfigurableApplicationContext context,
          MergedContextConfiguration mergedConfig) {

        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        TestPropertyValues testValues = TestPropertyValues.empty();
        EmbeddedRabbitMQ rabbitMQAnnotation = AnnotatedElementUtils.findMergedAnnotation(testClass,
            EmbeddedRabbitMQ.class);

        if (rabbitMQAnnotation != null) {
          Class<? extends RabbitMQTestContainer> containerClass = rabbitMQAnnotation.container();
          if (prodTestContainer == null) {
            try {
              prodTestContainer = beanFactory.createBean(containerClass);
              beanFactory.registerSingleton(containerClass.getName(), prodTestContainer);
              /**
               * ((DefaultListableBeanFactory)beanFactory).registerDisposableBean(containerClass.getName(), prodTestContainer);
               */
            } catch (Exception e) {
              throw new RuntimeException("Failed to create RabbitMQ container", e);
            }
          }

          testValues = testValues.and(
              "spring.rabbitmq.host=" + prodTestContainer.getTestContainer().getHost());
          testValues = testValues.and(
              "spring.rabbitmq.port=" + prodTestContainer.getTestContainer().getAmqpPort());
          testValues = testValues.and(
              "spring.rabbitmq.username=" + prodTestContainer.getTestContainer()
                  .getAdminUsername());
          testValues = testValues.and(
              "spring.rabbitmq.password=" + prodTestContainer.getTestContainer()
                  .getAdminPassword());
        }

        testValues.applyTo(context);
      }

      @Override
      public int hashCode() {
        return RabbitTestContainer.class.getName().hashCode();
      }

      @Override
      public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
      }
    };
  }
}
