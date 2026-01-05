package com.taskorchestrator.task_registry;


import com.taskorchestrator.task_registry.config.IntegrationTest;
import com.taskorchestrator.task_registry.repository.TaskGraphRepository;
import com.taskorchestrator.task_registry.repository.TaskTemplateRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.PlatformTransactionManager;

@IntegrationTest
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
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

  @Test
  void test() {
    System.out.println("test");
  }
}
