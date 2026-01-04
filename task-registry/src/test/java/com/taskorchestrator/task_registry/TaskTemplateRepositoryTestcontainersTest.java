package com.taskorchestrator.task_registry;


import static org.assertj.core.api.Assertions.assertThat;

import com.taskorchestrator.task_registry.TaskTemplateRepositoryTestcontainersTest.DataSourceInitializer;
import com.taskorchestrator.task_registry.entity.TaskDependencyEntity;
import com.taskorchestrator.task_registry.entity.TaskGraphEntity;
import com.taskorchestrator.task_registry.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry.enums.TaskCondition;
import com.taskorchestrator.task_registry.enums.TaskType;
import com.taskorchestrator.task_registry.repository.TaskGraphRepository;
import com.taskorchestrator.task_registry.repository.TaskTemplateRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ContextConfiguration(initializers = DataSourceInitializer.class)
public class TaskTemplateRepositoryTestcontainersTest {

  @Autowired
  TaskTemplateRepository taskTemplateRepository;
  @Autowired
  private TaskTemplateRepository templateRepo;
  @Autowired
  private TaskGraphRepository graphRepo;
  @Autowired
  private EntityManager entityManager;
  @Autowired
  private PlatformTransactionManager transactionManager;

  @Container
  static final PostgreSQLContainer<?> database =
      new PostgreSQLContainer<>("postgres:17-alpine").withUsername("postgres");

  static class DataSourceInitializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
          "spring.datasource.url=" + database.getJdbcUrl(),
          "spring.datasource.username=" + database.getUsername(),
          "spring.datasource.password=" + database.getPassword(),
          "spring.jpa.hibernate.ddl-auto=validate");
    }
  }

  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  void shouldSaveAndLoadCompleteTaskGraph() {
    // Начинаем транзакцию
    TransactionStatus status = transactionManager.getTransaction(
        new DefaultTransactionDefinition()
    );

    try {
      // 1. Создаём шаблоны задач
      TaskTemplateEntity task1 = createTemplate("validate-payment", TaskType.HTTP_CALL);
      TaskTemplateEntity task2 = createTemplate("process-payment", TaskType.DATABASE_QUERY);
      TaskTemplateEntity task3 = createTemplate("send-receipt", TaskType.HTTP_CALL);

      templateRepo.saveAll(List.of(task1, task2, task3));

      // 2. Создаём граф
      TaskGraphEntity graph = new TaskGraphEntity();
      graph.setName("Payment Workflow");
      graph.setTemplates(List.of(task1, task2, task3));

      // 3. Создаём зависимости
      TaskDependencyEntity dep1 = new TaskDependencyEntity();
      dep1.setGraph(graph);
      dep1.setParent(task1);
      dep1.setChild(task2);
      dep1.setCondition(TaskCondition.SUCCESS);

      TaskDependencyEntity dep2 = new TaskDependencyEntity();
      dep2.setGraph(graph);
      dep2.setParent(task2);
      dep2.setChild(task3);
      dep2.setCondition(TaskCondition.SUCCESS);

      graph.setDependencies(List.of(dep1, dep2));

      // 4. Сохраняем граф
      graphRepo.save(graph);
      entityManager.flush();
      entityManager.clear();

      // 5. Загружаем обратно (все ещё в транзакции!)
      TaskGraphEntity loadedGraph = graphRepo.findById(graph.getId()).orElseThrow();

      // Проверки РАБОТАЮТ (ленивые коллекции доступны)
      assertThat(loadedGraph.getName()).isEqualTo("Payment Workflow");
      assertThat(loadedGraph.getTemplates()).hasSize(3);
      assertThat(loadedGraph.getDependencies()).hasSize(2);

      // 6. Коммитим транзакцию
      transactionManager.commit(status);
      System.out.println("commit");
    } catch (Exception e) {
      transactionManager.rollback(status);
      throw e;
    }

  }

  private TaskTemplateEntity createTemplate(String name, TaskType type) {
    TaskTemplateEntity template = new TaskTemplateEntity();
    template.setName(name);
    template.setVersion("1.0");
    template.setType(type);
    template.setInputSchema(Map.of("type", "object"));
    template.setOutputSchema(Map.of("type", "object"));
    template.setConfig(Map.of("timeout", 5000));
    return template;
  }
}
