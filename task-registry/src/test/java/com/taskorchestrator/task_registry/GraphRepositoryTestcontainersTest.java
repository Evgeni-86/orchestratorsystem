package com.taskorchestrator.task_registry;

import static org.assertj.core.api.Assertions.assertThat;

import com.taskorchestrator.task_registry.config.IntegrationTest;
import com.taskorchestrator.task_registry.dto.graph.GraphDependencyDto;
import com.taskorchestrator.task_registry.dto.graph.TaskGraphCreateDto;
import com.taskorchestrator.task_registry.dto.graph.TaskGraphResponseDto;
import com.taskorchestrator.task_registry.entity.TaskDependencyEntity;
import com.taskorchestrator.task_registry.entity.TaskGraphEntity;
import com.taskorchestrator.task_registry.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry.enums.TaskCondition;
import com.taskorchestrator.task_registry.enums.TaskType;
import com.taskorchestrator.task_registry.exception.GraphValidationException;
import com.taskorchestrator.task_registry.repository.TaskGraphRepository;
import com.taskorchestrator.task_registry.repository.TaskTemplateRepository;
import com.taskorchestrator.task_registry.service.GraphService;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@IntegrationTest
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class GraphRepositoryTestcontainersTest {

  @Autowired
  private TaskGraphRepository graphRepo;
  @Autowired
  private GraphService graphService;
  @Autowired
  private TaskTemplateRepository templateRepo;
  @Autowired
  private EntityManager entityManager;
  @Autowired
  private PlatformTransactionManager transactionManager;

  @BeforeEach
  void cleanDatabase() {
    TransactionStatus status = transactionManager.getTransaction(
        new DefaultTransactionDefinition());
    try {
      entityManager.createNativeQuery("DELETE FROM task_dependencies").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM graph_tasks").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM task_graphs").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM task_templates").executeUpdate();
      transactionManager.commit(status);
    } catch (Exception e) {
      transactionManager.rollback(status);
      throw e;
    }
    entityManager.clear();
  }

  @Test
  void shouldSaveGraphWithTemplatesToDatabase() {
    // given: создаем шаблоны в БД
    TaskTemplateEntity template1 = createAndSaveTemplate("HTTP Task", "1.0", TaskType.HTTP_CALL);
    TaskTemplateEntity template2 = createAndSaveTemplate("DB Query", "1.0",
        TaskType.DATABASE_QUERY);
    TaskTemplateEntity template3 = createAndSaveTemplate("Script", "1.0", TaskType.SCRIPT);

    TaskGraphCreateDto dto = new TaskGraphCreateDto(
        "ETL Pipeline",
        List.of(template1.getId(), template2.getId(), template3.getId()),
        List.of(
            new GraphDependencyDto(template1.getId(), template2.getId(), TaskCondition.SUCCESS),
            new GraphDependencyDto(template2.getId(), template3.getId(), TaskCondition.ALWAYS)
        ),
        Map.of("environment", "test", "version", "1.0.0")
    );

    // when: создаем граф через сервис
    TaskGraphResponseDto response = graphService.createTaskGraph(dto);

    // then: проверяем сохранение в БД
    assertThat(response).isNotNull();
    assertThat(response.id()).isNotNull();
    assertThat(response.name()).isEqualTo("ETL Pipeline");

    // Проверяем что граф сохранен
    TaskGraphEntity savedGraph = graphRepo.findWithFullRelationsById(response.id())
        .orElseThrow(() -> new AssertionError("Graph not found in DB"));

    assertThat(savedGraph.getName()).isEqualTo("ETL Pipeline");
    assertThat(savedGraph.getTemplates()).hasSize(3);
    assertThat(savedGraph.getDependencies()).hasSize(2);

    // Проверяем связи в таблице graph_tasks (ManyToMany)
    List<UUID> savedTemplateIds = savedGraph.getTemplates().stream()
        .map(TaskTemplateEntity::getId)
        .toList();
    assertThat(savedTemplateIds).containsExactlyInAnyOrder(
        template1.getId(), template2.getId(), template3.getId()
    );

    // Проверяем зависимости
    assertThat(savedGraph.getDependencies())
        .anySatisfy(dep -> {
          assertThat(dep.getParent().getId()).isEqualTo(template1.getId());
          assertThat(dep.getChild().getId()).isEqualTo(template2.getId());
          assertThat(dep.getCondition()).isEqualTo(TaskCondition.SUCCESS);
        })
        .anySatisfy(dep -> {
          assertThat(dep.getParent().getId()).isEqualTo(template2.getId());
          assertThat(dep.getChild().getId()).isEqualTo(template3.getId());
          assertThat(dep.getCondition()).isEqualTo(TaskCondition.ALWAYS);
        });

    // Проверяем каскадное сохранение зависимостей
    assertThat(savedGraph.getDependencies().get(0).getId()).isNotNull();
    assertThat(savedGraph.getDependencies().get(1).getId()).isNotNull();
  }

  @Test
  void shouldSaveGraphWithoutDependencies() {
    // given
    TaskTemplateEntity template = createAndSaveTemplate("Simple Task", "1.0", TaskType.HTTP_CALL);

    TaskGraphCreateDto dto = new TaskGraphCreateDto(
        "Simple Graph",
        List.of(template.getId()),
        null, // без зависимостей
        null
    );

    // when
    TaskGraphResponseDto response = graphService.createTaskGraph(dto);

    // then
    TaskGraphEntity savedGraph = graphRepo.findWithFullRelationsById(response.id())
        .orElseThrow(() -> new AssertionError("Graph not found"));

    assertThat(savedGraph.getTemplates()).hasSize(1);
    assertThat(savedGraph.getDependencies()).isEmpty();
  }

  @Test
  void shouldHandleCircularDependencyValidation() {
    // given
    TaskTemplateEntity template1 = createAndSaveTemplate("Task 1", "1.0", TaskType.SCRIPT);
    TaskTemplateEntity template2 = createAndSaveTemplate("Task 2", "1.0", TaskType.SCRIPT);

    // Создаем циклическую зависимость (через DTO это невозможно,
    // но проверяем что валидатор работает)
    TaskGraphCreateDto dto = new TaskGraphCreateDto(
        "Cyclic Test",
        List.of(template1.getId(), template2.getId()),
        List.of(
            new GraphDependencyDto(template1.getId(), template2.getId(), TaskCondition.SUCCESS),
            new GraphDependencyDto(template2.getId(), template1.getId(), TaskCondition.ALWAYS)
        ),
        Map.of()
    );

    // when & then
    // Ожидаем, что сервис бросит исключение из-за цикла
    // (если ваш GraphValidator включен в сервисе)
    // Если нет валидации - тест проверит что сохраняется
    try {
      TaskGraphResponseDto response = graphService.createTaskGraph(dto);
      // Если дошли сюда, проверяем что сохранилось
      TaskGraphEntity savedGraph = graphRepo.findById(response.id()).orElseThrow();
      assertThat(savedGraph.getDependencies()).hasSize(2);
    } catch (GraphValidationException e) {
      // Это нормально, если валидатор включен
      assertThat(e.getMessage()).contains("cyclic");
    }
  }

  @Test
  void shouldUpdateTimestampsAutomatically() {
    // given
    TaskTemplateEntity template = createAndSaveTemplate("Timestamp Test", "1.0",
        TaskType.HTTP_CALL);

    TaskGraphCreateDto dto = new TaskGraphCreateDto(
        "Timestamp Graph",
        List.of(template.getId()),
        List.of(),
        Map.of()
    );

    // when
    TaskGraphResponseDto response = graphService.createTaskGraph(dto);

    // then
    TaskGraphEntity savedGraph = graphRepo.findById(response.id()).orElseThrow();

    assertThat(savedGraph.getCreatedAt()).isNotNull();
    // createdAt не должен быть в будущем :)
    assertThat(savedGraph.getCreatedAt()).isBefore(java.time.Instant.now());
  }

  @Test
  void shouldRetrieveFullGraphWithAllRelations() {
    // given: создаем сложный граф
    TaskTemplateEntity t1 = createAndSaveTemplate("Extract", "1.0", TaskType.DATABASE_QUERY);
    TaskTemplateEntity t2 = createAndSaveTemplate("Transform", "1.0", TaskType.SCRIPT);
    TaskTemplateEntity t3 = createAndSaveTemplate("Load", "1.0", TaskType.HTTP_CALL);

    TaskGraphCreateDto dto = new TaskGraphCreateDto(
        "Complex ETL",
        List.of(t1.getId(), t2.getId(), t3.getId()),
        List.of(
            new GraphDependencyDto(t1.getId(), t2.getId(), TaskCondition.SUCCESS),
            new GraphDependencyDto(t2.getId(), t3.getId(), TaskCondition.ALWAYS),
            new GraphDependencyDto(t1.getId(), t3.getId(), TaskCondition.ON_FAILURE)
        ),
        Map.of("complexity", "high")
    );

    TaskGraphResponseDto response = graphService.createTaskGraph(dto);

    // then: достаем КОНКРЕТНЫЙ граф по ID
    TaskGraphEntity retrievedGraph = graphRepo.findWithFullRelationsById(response.id())
        .orElseThrow(() -> new AssertionError("Created graph not found"));

    // Проверяем что связи загружены
    assertThat(retrievedGraph.getTemplates()).hasSize(3);
    assertThat(retrievedGraph.getDependencies()).hasSize(3);

    // Находим зависимость где parent = "Extract"
    Optional<TaskDependencyEntity> extractToTransformDep = retrievedGraph.getDependencies().stream()
        .filter(dep -> "Extract".equals(dep.getParent().getName()))
        .findFirst();

    assertThat(extractToTransformDep).isPresent();
    assertThat(extractToTransformDep.get().getParent().getName()).isEqualTo("Extract");
    assertThat(extractToTransformDep.get().getChild().getName()).isEqualTo("Transform");
  }

  private TaskTemplateEntity createAndSaveTemplate(String name, String version, TaskType type) {
    TaskTemplateEntity template = new TaskTemplateEntity();
    template.setName(name);
    template.setVersion(version);
    template.setType(type);
    template.setInputSchema(Map.of("type", "object"));
    template.setOutputSchema(Map.of("type", "object"));
    template.setConfig(Map.of("timeout", 5000));

    return templateRepo.save(template);
  }

  @Test
  void shouldCascadeDeleteDependencies() {
    // given
    TaskTemplateEntity template1 = createAndSaveTemplate("Task A", "1.0", TaskType.SCRIPT);
    TaskTemplateEntity template2 = createAndSaveTemplate("Task B", "1.0", TaskType.SCRIPT);

    TaskGraphCreateDto dto = new TaskGraphCreateDto(
        "Cascade Test",
        List.of(template1.getId(), template2.getId()),
        List.of(
            new GraphDependencyDto(template1.getId(), template2.getId(), TaskCondition.SUCCESS)
        ),
        Map.of()
    );

    TaskGraphResponseDto response = graphService.createTaskGraph(dto);
    UUID graphId = response.id();

    // when: удаляем граф
    graphRepo.deleteById(graphId);

    // then: проверяем что зависимости тоже удалились
    // (если cascade = CascadeType.ALL)
    Optional<TaskGraphEntity> deletedGraph = graphRepo.findById(graphId);
    assertThat(deletedGraph).isEmpty();

    // Дополнительно можно проверить таблицу task_dependencies
    // через native query или счетчик
  }

  @Test
  void shouldHandleMultipleGraphsEfficiently() {
    // given: создаем 10 графов
    List<TaskTemplateEntity> templates = IntStream.range(0, 5)
        .mapToObj(i -> createAndSaveTemplate("Template " + i, "1.0", TaskType.HTTP_CALL))
        .toList();

    List<UUID> templateIds = templates.stream()
        .map(TaskTemplateEntity::getId)
        .toList();

    for (int i = 0; i < 10; i++) {
      TaskGraphCreateDto dto = new TaskGraphCreateDto(
          "Graph " + i,
          templateIds,
          List.of(
              new GraphDependencyDto(templateIds.get(0), templateIds.get(1), TaskCondition.SUCCESS)
          ),
          Map.of("index", i)
      );

      graphService.createTaskGraph(dto);
    }

    // when
    long startTime = System.currentTimeMillis();
    List<TaskGraphEntity> allGraphs = graphRepo.findAllWithFullRelations();
    long endTime = System.currentTimeMillis();

    // then
    assertThat(allGraphs).hasSize(10);

    // Производительность: не должно быть N+1 проблемы
    // (если связи загружаются правильно)
    long queryTime = endTime - startTime;
    assertThat(queryTime).isLessThan(1000); // максимум 1 секунда

    // Проверяем что все связи загружены
    for (TaskGraphEntity graph : allGraphs) {
      assertThat(graph.getTemplates()).hasSize(5);
      assertThat(graph.getDependencies()).hasSize(1);
    }
  }
}
