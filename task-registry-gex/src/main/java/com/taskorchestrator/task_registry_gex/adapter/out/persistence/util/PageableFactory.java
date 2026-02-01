package com.taskorchestrator.task_registry_gex.adapter.out.persistence.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableFactory {

  private PageableFactory() {
  }

  public static Pageable build(Pageable pageable, Sort sort) {
    return PageRequest.of(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        sort
    );
  }
}