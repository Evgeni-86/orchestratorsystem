package com.taskorchestrator.task_registry_gex.adapter.out.persistence.repository.specification;

import com.taskorchestrator.task_registry_gex.adapter.in.web.dto.task.TaskTemplateFilterDto;
import com.taskorchestrator.task_registry_gex.adapter.out.persistence.entity.TaskTemplateEntity;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TaskTemplateSpecificationBuilder {

  public Specification<TaskTemplateEntity> build(TaskTemplateFilterDto filter) {
    return Stream.of(
            createSpecification(filter.type(), this::typeEquals)
        )
        .flatMap(Optional::stream)
        .reduce(Specification::and)
        .orElse(
            (root, query, cb) -> cb.conjunction()
        );
  }

  private <V> Optional<Specification<TaskTemplateEntity>> createSpecification(
      V value,
      Function<V, Specification<TaskTemplateEntity>> function
  ) {
    if (value instanceof String stringValue) {
      return StringUtils.hasText(stringValue)
          ? Optional.of(function.apply(value))
          : Optional.empty();
    }

    return Optional.ofNullable(value).map(function);
  }

  private Specification<TaskTemplateEntity> typeEquals(String type) {
    return (root, query, cb) -> cb.equal(root.get("type"), type);
  }
}
