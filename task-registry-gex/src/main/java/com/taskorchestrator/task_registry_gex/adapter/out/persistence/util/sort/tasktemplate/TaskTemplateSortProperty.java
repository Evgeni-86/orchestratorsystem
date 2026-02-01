package com.taskorchestrator.task_registry_gex.adapter.out.persistence.util.sort.tasktemplate;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.util.sort.core.SortableProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum TaskTemplateSortProperty implements SortableProperty {
  /**
   * Сортировать по новым taskTemplate
   */
  NEW(Sort.by(Sort.Direction.DESC, "createdAt")),
  /**
   * Сортировать по старым taskTemplate
   */
  OLDER(Sort.by(Sort.Direction.ASC, "createdAt"));

  private final Sort sort;
}
