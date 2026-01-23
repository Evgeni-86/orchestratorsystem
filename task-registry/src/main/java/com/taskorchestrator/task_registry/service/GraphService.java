package com.taskorchestrator.task_registry.service;

import com.taskorchestrator.task_registry.domain.TaskDependency;
import com.taskorchestrator.task_registry.domain.TaskGraph;
import com.taskorchestrator.task_registry.domain.TaskGraphEventPayload;
import com.taskorchestrator.task_registry.domain.TaskGraphOutboxMessage;
import com.taskorchestrator.task_registry.domain.TaskTemplate;
import com.taskorchestrator.task_registry.domain.validator.GraphValidator;
import com.taskorchestrator.task_registry.domain.validator.ValidationResult;
import com.taskorchestrator.task_registry.dto.graph.GraphValidateResultResponseDto;
import com.taskorchestrator.task_registry.dto.graph.TaskGraphCreateDto;
import com.taskorchestrator.task_registry.dto.graph.TaskGraphResponseDto;
import com.taskorchestrator.task_registry.entity.TaskDependencyEntity;
import com.taskorchestrator.task_registry.entity.TaskGraphEntity;
import com.taskorchestrator.task_registry.entity.TaskTemplateEntity;
import com.taskorchestrator.task_registry.enums.OutboxStatus;
import com.taskorchestrator.task_registry.exception.GraphValidationException;
import com.taskorchestrator.task_registry.exception.ObjectNotFoundException;
import com.taskorchestrator.task_registry.mapper.graph.GraphDirectMapper;
import com.taskorchestrator.task_registry.mapper.graph.GraphDtoMapper;
import com.taskorchestrator.task_registry.mapper.graph.GraphEntityMapper;
import com.taskorchestrator.task_registry.repository.TaskGraphRepository;
import com.taskorchestrator.task_registry.repository.TaskTemplateRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphService {

  private final GraphValidator graphValidator = new GraphValidator();
  private final TaskGraphRepository taskGraphRepository;
  private final TaskTemplateRepository taskTemplateRepository;
  private final GraphDtoMapper graphDtoMapper;
  private final GraphDirectMapper graphDirectMapper;
  private final GraphEntityMapper graphEntityMapper;
  private final OutboxService outboxService;

  @Transactional
  public TaskGraphResponseDto createTaskGraph(TaskGraphCreateDto taskGraphCreateDto) {
    log.debug("Creating task graph from DTO: {}", taskGraphCreateDto.name());

    // 1. DTO → Domain (чистый маппинг)
    TaskGraph domainGraph = graphDtoMapper.createDtoToDomain(taskGraphCreateDto);

    // 2. Валидация графа (используем валидатор)
    ValidationResult validationResult = graphValidator.validate(domainGraph);
    if (!validationResult.isValid()) {
      throw new GraphValidationException(
          "Graph validation failed: " + String.join(", ", validationResult.getErrors())
      );
    }

    // 3. Загрузка TemplateEntity из БД (упрощенная версия)
    List<TaskTemplateEntity> templates = loadTemplates(domainGraph);

    List<UUID> entryPoints = graphValidator.findEntryPoints(domainGraph).stream()
        .map(UUID::fromString).toList();

    // 4. Создание Entity графа
    TaskGraphEntity entity = createGraphEntity(domainGraph, templates);

    // 5. Сохранение
    TaskGraphEntity savedEntity = taskGraphRepository.save(entity);
    TaskGraphOutboxMessage message = createOutboxMessage(savedEntity);
    outboxService.save(message);
    log.info("Created task graph with id: {}", savedEntity.getId());

    return graphDirectMapper.entityToResponseDtoWithEntryPoint(savedEntity, entryPoints);
  }

  private TaskGraphOutboxMessage createOutboxMessage(TaskGraphEntity savedEntity) {
    TaskGraphEventPayload payload = TaskGraphEventPayload.builder()
        .graphId(savedEntity.getId().toString())
        .createdAt(savedEntity.getCreatedAt().toString())
        .build();
    return TaskGraphOutboxMessage.builder()
        .payload(payload)
        .outboxStatus(OutboxStatus.PENDING)
        .build();
  }

  private List<TaskTemplateEntity> loadTemplates(TaskGraph domainGraph) {
    List<UUID> templateIds = domainGraph.getTasks().stream()
        .map(TaskTemplate::getId).toList();

    List<TaskTemplateEntity> templates = taskTemplateRepository.findAllById(templateIds);

    // Ваш валидатор уже проверил существование всех ID,
    // но делаем дополнительную проверку для надежности
    if (templates.size() != templateIds.size()) {
      Set<UUID> foundIds = templates.stream()
          .map(TaskTemplateEntity::getId)
          .collect(Collectors.toSet());

      List<UUID> missing = templateIds.stream()
          .filter(id -> !foundIds.contains(id)).toList();

      throw new ObjectNotFoundException("Task templates not found: " + missing);
    }

    return templates;
  }

  private TaskGraphEntity createGraphEntity(TaskGraph domainGraph,
      List<TaskTemplateEntity> templates) {
    TaskGraphEntity entity = new TaskGraphEntity();
    entity.setName(domainGraph.getName());
    entity.setTemplates(templates);

    // Создаем зависимости только если они есть
    if (domainGraph.getDependencies() != null && !domainGraph.getDependencies().isEmpty()) {
      Map<UUID, TaskTemplateEntity> templateMap = createTemplateMap(templates);
      List<TaskDependencyEntity> dependencies = createDependencies(
          domainGraph.getDependencies(),
          entity,
          templateMap
      );
      entity.setDependencies(dependencies);
    }

    return entity;
  }

  private Map<UUID, TaskTemplateEntity> createTemplateMap(List<TaskTemplateEntity> templates) {
    return templates.stream()
        .collect(Collectors.toMap(TaskTemplateEntity::getId, Function.identity()));
  }

  private List<TaskDependencyEntity> createDependencies(
      List<TaskDependency> domainDependencies,
      TaskGraphEntity graphEntity,
      Map<UUID, TaskTemplateEntity> templateMap
  ) {
    return domainDependencies.stream()
        .map(domainDep -> {
          TaskDependencyEntity entity = new TaskDependencyEntity();
          entity.setGraph(graphEntity);
          entity.setParent(templateMap.get(domainDep.getParentTaskId()));
          entity.setChild(templateMap.get(domainDep.getChildTaskId()));
          entity.setCondition(domainDep.getCondition());
          return entity;
        }).toList();
  }

  public GraphValidateResultResponseDto validateGraphById(String id) {
    TaskGraphEntity graph = taskGraphRepository.findById(UUID.fromString(id))
        .orElseThrow(() -> new ObjectNotFoundException("Graph not found with {id}: " + id));
    ValidationResult validationResult = graphValidator.validate(graphEntityMapper.toDomain(graph));
    return graphDtoMapper.validateToResponseDto(validationResult);
  }

  @Transactional(readOnly = true)
  public TaskGraphResponseDto findById(String id) {
    TaskGraphEntity graph = taskGraphRepository.findWithFullRelationsById(UUID.fromString(id))
        .orElseThrow(() -> new ObjectNotFoundException("Graph not found with {id}: " + id));
    TaskGraph graphDomain = graphEntityMapper.toDomain(graph);
    List<UUID> entryPoints = graphValidator.findEntryPoints(graphDomain)
        .stream()
        .map(UUID::fromString).toList();
    return graphDirectMapper.entityToResponseDtoWithEntryPoint(graph, entryPoints);
  }
}
