package com.taskorchestrator.task_registry.domain.validator;

import com.taskorchestrator.task_registry.domain.TaskDependency;
import com.taskorchestrator.task_registry.domain.TaskGraph;
import com.taskorchestrator.task_registry.domain.TaskTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Простой валидатор графов зависимостей задач Основная функция: проверка на циклические
 * зависимости
 */
public class GraphValidator {

  /**
   * Проверяет, содержит ли граф циклические зависимости
   *
   * @param graph граф задач для проверки
   * @return true если найден цикл, false если циклов нет
   */
  public boolean hasCycles(TaskGraph graph) {
    // 1. Строим список смежности
    Map<String, List<String>> adjacencyList = buildAdjacencyList(graph);

    // 2. Структуры для отслеживания состояния вершин
    Set<String> visited = new HashSet<>();      // Полностью обработанные вершины
    Set<String> recursionStack = new HashSet<>(); // Вершины в текущем пути обхода

    // 3. Запускаем DFS из каждой непосещенной вершины
    for (String taskId : adjacencyList.keySet()) {
      if (hasCycleUtil(taskId, visited, recursionStack, adjacencyList)) {
        return true; // Найден цикл!
      }
    }

    return false; // Циклов не обнаружено
  }

  /**
   * Рекурсивный вспомогательный метод для поиска циклов
   *
   * @param currentTask    текущая обрабатываемая вершина
   * @param visited        множество полностью обработанных вершин
   * @param recursionStack стек рекурсии (текущий путь обхода)
   * @param adjacencyList  список смежности графа
   * @return true если найден цикл
   */
  private boolean hasCycleUtil(String currentTask,
      Set<String> visited,
      Set<String> recursionStack,
      Map<String, List<String>> adjacencyList) {

    // Если вершина уже в текущем стеке рекурсии - мы нашли цикл!
    if (recursionStack.contains(currentTask)) {
      return true;
    }

    // Если вершина уже полностью обработана - пропускаем
    if (visited.contains(currentTask)) {
      return false;
    }

    // Помечаем вершину как посещенную и добавляем в стек рекурсии
    visited.add(currentTask);
    recursionStack.add(currentTask);

    // Рекурсивно проверяем всех соседей (зависимости)
    List<String> dependencies = adjacencyList.getOrDefault(currentTask, new ArrayList<>());
    for (String dependency : dependencies) {
      if (hasCycleUtil(dependency, visited, recursionStack, adjacencyList)) {
        return true; // Цикл найден в поддереве
      }
    }

    // Удаляем вершину из стека рекурсии (backtracking)
    recursionStack.remove(currentTask);

    return false;
  }

  /**
   * Строит список смежности для графа Формат: taskId -> [зависимости этой задачи]
   *
   * @param graph исходный граф
   * @return список смежности
   */
  private Map<String, List<String>> buildAdjacencyList(TaskGraph graph) {
    Map<String, List<String>> adjacencyList = new HashMap<>();

    // Инициализируем все вершины
    for (TaskTemplate taskTemplate : graph.getTasks()) {
      adjacencyList.put(taskTemplate.getId(), new ArrayList<>());
    }

    // Добавляем ребра (зависимости)
    for (TaskDependency taskDependency : graph.getDependencies()) {
      // taskDependency: child зависит от parent
      String childId = taskDependency.getChildTaskId();
      String parentId = taskDependency.getParentTaskId();

      // Добавляем parent в список зависимостей child
      adjacencyList.get(childId).add(parentId);
    }

    return adjacencyList;
  }

  /**
   * Простая валидация графа (базовые проверки)
   *
   * @param graph граф для валидации
   * @return результат валидации
   */
  public ValidationResult validate(TaskGraph graph) {
    ValidationResult result = new ValidationResult();

    // 1. Проверка на циклы
    if (hasCycles(graph)) {
      result.addError("Graph contains cyclic dependencies");
    }

    // 2. Проверка ссылок на несуществующие задачи
    Set<String> taskIds = new HashSet<>();
    for (TaskTemplate taskTemplate : graph.getTasks()) {
      taskIds.add(taskTemplate.getId());
    }

    for (TaskDependency taskDependency : graph.getDependencies()) {
      if (!taskIds.contains(taskDependency.getParentTaskId())) {
        result.addError("Parent task not found: " + taskDependency.getParentTaskId());
      }
      if (!taskIds.contains(taskDependency.getChildTaskId())) {
        result.addError("Child task not found: " + taskDependency.getChildTaskId());
      }
    }

    // 3. Проверка на наличие хотя бы одной задачи
    if (graph.getTasks().isEmpty()) {
      result.addError("Graph has no tasks");
    }

    result.setValid(result.getErrors().isEmpty());
    return result;
  }

  /**
   * Вспомогательный метод: находит входные узлы (задачи без зависимостей)
   */
  public List<String> findEntryPoints(TaskGraph graph) {
    Map<String, List<String>> adjacencyList = buildAdjacencyList(graph);
    List<String> entryPoints = new ArrayList<>();

    for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
      // Задача является входной, если у нее нет зависимостей
      if (entry.getValue().isEmpty()) {
        entryPoints.add(entry.getKey());
      }
    }

    return entryPoints;
  }

  /**
   * Проверяет, может ли граф быть выполнен (есть входные узлы)
   */
  public boolean canExecute(TaskGraph graph) {
    return !findEntryPoints(graph).isEmpty();
  }
}
