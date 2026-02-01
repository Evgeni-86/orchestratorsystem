package com.taskorchestrator.task_registry_gex.infrastructure.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@SuppressWarnings("java:S2638")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @SuppressWarnings("unused")
  @Value("${server.error.include-stacktrace:NEVER}")
  private ErrorProperties.IncludeAttribute includeStackTrace;

  @Override
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception,
      @NonNull
      HttpHeaders headers,
      @NonNull
      HttpStatusCode status,
      @NonNull
      WebRequest request
  ) {
    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.UNPROCESSABLE_ENTITY.value(),
        "Ошибка валидации. Проверьте поле \"errors\" за подробностями."
    );

    exception.getBindingResult().getFieldErrors().forEach(
        fieldError -> errorResponse
            .addValidationError(fieldError.getField(), fieldError.getDefaultMessage())
    );

    exception.getBindingResult().getGlobalErrors().forEach(
        globalError -> errorResponse
            .addValidationError(globalError.getObjectName(), globalError.getDefaultMessage())
    );

    return ResponseEntity.unprocessableEntity().body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleAllUncaughtException(
      Exception exception,
      WebRequest request
  ) {
    final String errorMessage = "Произошла неизвестная ошибка.";

    return buildErrorResponse(exception, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<Object> handleDataIntegrityViolationException(
      DataIntegrityViolationException exception,
      WebRequest request
  ) {
    final String errorMessage = "Нарушение целостности данных. Проверьте связанные сущности.";

    return buildErrorResponse(
        exception,
        errorMessage,
        HttpStatus.CONFLICT
    );
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ResponseEntity<Object> handleConstraintViolationException(
      ConstraintViolationException exception,
      WebRequest request
  ) {
    return buildErrorResponse(exception, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(ObjectNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleObjectNotFoundException(
      ObjectNotFoundException exception,
      WebRequest request
  ) {
    return buildErrorResponse(
        exception,
        exception.getMessage(),
        HttpStatus.NOT_FOUND
    );
  }

  @ExceptionHandler(DataBindingViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<Object> handleDataBindingViolationException(
      DataBindingViolationException exception,
      WebRequest request
  ) {
    return buildErrorResponse(
        exception,
        exception.getMessage(),
        HttpStatus.CONFLICT
    );
  }

  // Inspect
  private ResponseEntity<Object> buildErrorResponse(
      Exception exception,
      HttpStatus httpStatus
  ) {
    return buildErrorResponse(
        exception,
        exception.getMessage(),
        httpStatus
    );
  }

  private ResponseEntity<Object> buildErrorResponse(
      Exception exception,
      String message,
      HttpStatus httpStatus
  ) {
    ErrorResponse errorResponse = new ErrorResponse(
        httpStatus.value(),
        message
    );

    if (includeStackTrace == ErrorProperties.IncludeAttribute.ALWAYS) {
      errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
    }

    return ResponseEntity.status(httpStatus).body(errorResponse);
  }
}

