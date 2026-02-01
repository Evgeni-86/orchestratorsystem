package com.taskorchestrator.task_registry_gex.adapter.out.persistence.util.sort.tasktemplate;

import com.taskorchestrator.task_registry_gex.adapter.out.persistence.util.sort.core.SortResolver;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class TaskTemplateSortResolver extends SortResolver<TaskTemplateSortProperty> {

  public TaskTemplateSortResolver() {
    super(
        TaskTemplateSortProperty.class,
        Sort.by(Sort.Direction.DESC, "createdAt")
    );
  }
}
