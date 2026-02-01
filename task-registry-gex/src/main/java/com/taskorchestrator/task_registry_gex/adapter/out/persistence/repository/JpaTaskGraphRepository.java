package com.taskorchestrator.task_registry_gex.adapter.out.persistence.repository;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskGraphEntity;
import com.taskorchestrator.task_registry_gex.application.core.port.out.TaskGraphRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTaskGraphRepository extends JpaRepository<TaskGraphEntity, UUID>,
    TaskGraphRepository {

  // Граф с шаблонами
  @Query("SELECT g FROM TaskGraphEntity g LEFT JOIN FETCH g.templates WHERE g.id = :id")
  Optional<TaskGraphEntity> findByIdWithTemplates(@Param("id") UUID id);

  // Граф с зависимостями
  @Query("""
      SELECT g FROM TaskGraphEntity g
      LEFT JOIN FETCH g.dependencies d
      LEFT JOIN FETCH d.parent
      LEFT JOIN FETCH d.child
      WHERE g.id = :id
      """)
  Optional<TaskGraphEntity> findByIdWithDependencies(@Param("id") UUID id);

  // Default метод, который объединяет результаты
  default Optional<TaskGraphEntity> findWithFullRelationsById(UUID id) {
    // Загружаем граф с шаблонами
    Optional<TaskGraphEntity> withTemplates = findByIdWithTemplates(id);
    if (withTemplates.isEmpty()) {
      return Optional.empty();
    }

    TaskGraphEntity result = withTemplates.get();

    // Загружаем граф с зависимостями
    Optional<TaskGraphEntity> withDependencies = findByIdWithDependencies(id);
    withDependencies.ifPresent(g ->
        // Копируем зависимости в наш результат
        result.setDependencies(g.getDependencies())
    );

    return Optional.of(result);
  }

  // Все графы с шаблонами
  @Query("SELECT DISTINCT g FROM TaskGraphEntity g LEFT JOIN FETCH g.templates")
  List<TaskGraphEntity> findAllWithTemplates();

  // Все графы с зависимостями
  @Query("""
      SELECT DISTINCT g FROM TaskGraphEntity g
      LEFT JOIN FETCH g.dependencies d
      LEFT JOIN FETCH d.parent
      LEFT JOIN FETCH d.child
      """)
  List<TaskGraphEntity> findAllWithDependencies();

  // Все графы со всеми связями (объединяем)
  default List<TaskGraphEntity> findAllWithFullRelations() {
    // Загружаем с шаблонами
    List<TaskGraphEntity> withTemplates = findAllWithTemplates();

    // Загружаем с зависимостями
    List<TaskGraphEntity> withDependencies = findAllWithDependencies();

    // Объединяем: для каждого графа добавляем зависимости
    Map<UUID, TaskGraphEntity> graphMap = new HashMap<>();

    // Сначала все графы с шаблонами
    for (TaskGraphEntity graph : withTemplates) {
      graphMap.put(graph.getId(), graph);
    }

    // Добавляем зависимости из второго запроса
    for (TaskGraphEntity graphWithDeps : withDependencies) {
      TaskGraphEntity graph = graphMap.get(graphWithDeps.getId());
      if (graph != null) {
        graph.setDependencies(graphWithDeps.getDependencies());
      }
    }

    return new ArrayList<>(graphMap.values());
  }
}
