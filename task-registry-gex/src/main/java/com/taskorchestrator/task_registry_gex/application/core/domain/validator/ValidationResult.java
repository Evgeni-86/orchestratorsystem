package com.taskorchestrator.task_registry_gex.application.core.domain.validator;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Результат валидации графа
 */
@Getter
@NoArgsConstructor
public class ValidationResult {

  @Setter
  private boolean isValid;
  private final List<String> errors = new ArrayList<>();
  private final List<String> warnings = new ArrayList<>();

  public void addError(String error) {
    this.errors.add(error);
  }

  public void addWarning(String warning) {
    this.warnings.add(warning);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("ValidationResult{isValid=").append(isValid);

    if (!errors.isEmpty()) {
      sb.append(", errors=").append(errors);
    }

    if (!warnings.isEmpty()) {
      sb.append(", warnings=").append(warnings);
    }

    sb.append("}");
    return sb.toString();
  }
}
