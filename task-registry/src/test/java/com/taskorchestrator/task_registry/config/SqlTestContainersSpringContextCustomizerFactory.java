package com.taskorchestrator.task_registry.config;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;

public class SqlTestContainersSpringContextCustomizerFactory implements ContextCustomizerFactory {

  private static final ConcurrentHashMap<Class<? extends SqlTestContainer>, SqlTestContainer>
      containers = new ConcurrentHashMap<>();

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
        EmbeddedSQL sqlAnnotation = AnnotatedElementUtils.findMergedAnnotation(testClass,
            EmbeddedSQL.class);

        if (sqlAnnotation != null) {
          Class<? extends SqlTestContainer> containerClass = sqlAnnotation.container();
          SqlTestContainer container = containers.computeIfAbsent(containerClass, key -> {
            try {
              SqlTestContainer newContainer = beanFactory.createBean(key);
              if (!beanFactory.containsSingleton(key.getName())) {
                beanFactory.registerSingleton(key.getName(), newContainer);
              }
              return newContainer;
            } catch (Exception e) {
              throw new RuntimeException(
                  "Failed to create SQL container: " + key.getName(), e);
            }
          });
          testValues = testValues.and(
              "spring.datasource.url=" + container.getTestContainer().getJdbcUrl() + "");
          testValues = testValues.and(
              "spring.datasource.username=" + container.getTestContainer().getUsername());
          testValues = testValues.and(
              "spring.datasource.password=" + container.getTestContainer().getPassword());
          testValues = testValues.and("spring.jpa.hibernate.ddl-auto=create-drop");
        }

        testValues.applyTo(context);
      }

      @Override
      public int hashCode() {
        return SqlTestContainer.class.getName().hashCode();
      }

      @Override
      public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
      }
    };
  }
}
