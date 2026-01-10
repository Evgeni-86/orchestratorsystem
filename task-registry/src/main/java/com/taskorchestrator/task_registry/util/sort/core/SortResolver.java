package com.taskorchestrator.task_registry.util.sort.core;

import java.util.Locale;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

public class SortResolver<E extends Enum<E> & SortableProperty> {

  private final Class<E> enumClass;
  private final Sort defaultSort;

  protected SortResolver(Class<E> enumClass, Sort defaultSort) {
    this.enumClass = enumClass;
    this.defaultSort = defaultSort;
  }

  public Sort resolve(String sortParam) {
    if (!StringUtils.hasText(sortParam)) {
      return defaultSort;
    }

    try {
      E sortProperty = Enum.valueOf(enumClass, sortParam.toUpperCase(Locale.ROOT));

      return sortProperty.getSort();
    } catch (IllegalArgumentException ex) {
      return defaultSort;
    }
  }
}
