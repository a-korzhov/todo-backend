package com.korzhov.todo.exception;

import com.korzhov.todo.dto.error.ErrorDetails;
import com.korzhov.todo.dto.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {Exception.class})
  protected ResponseEntity<?> handleException(
      Exception ex
  ) {
    ErrorResponse response = ErrorResponse.builder()
        .message(ex.getMessage())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
        .build();
    log.error(ex.getMessage(), ex);
    return ResponseEntity.internalServerError().body(response);
  }

  @ExceptionHandler(value = {ConstraintViolationException.class})
  protected ResponseEntity<?> handleConstraintViolationException(
      ConstraintViolationException ex
  ) {
    List<ErrorDetails> details = ex.getConstraintViolations().stream()
        .map(
            cv -> ErrorDetails.builder()
                .issue(cv.getMessage())
                .build()
        ).collect(Collectors.toList());
    return getBadRequestErrorResponse(details);
  }

  @Override
  @NonNull
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      @NonNull MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatus status,
      @NonNull WebRequest request
  ) {
    List<ErrorDetails> errors = new ArrayList<>();
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    for (FieldError f : fieldErrors) {
      errors.add(
          ErrorDetails.builder()
              .field(f.getField())
              .issue(f.getDefaultMessage())
              .build());
    }
    ErrorResponse errorResponse = ErrorResponse.builder()
        .id(UUID.randomUUID().toString())
        .status(status.toString())
        .details(errors)
        .build();
    log.error(ex.getMessage());
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(value = {ResourceAlreadyExistsException.class})
  protected ResponseEntity<?> handleResourceExistsException(
      ResourceAlreadyExistsException ex
  ) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .id(UUID.randomUUID().toString())
        .status(HttpStatus.UNPROCESSABLE_ENTITY.toString())
        .message(ex.getMessage())
        .build();
    log.warn(ex.getMessage());
    return ResponseEntity.unprocessableEntity().body(errorResponse);
  }

  @ExceptionHandler(value = {ResourceNotFoundException.class})
  protected ResponseEntity<?> handleResourceNotFoundException(
      ResourceNotFoundException ex
  ) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .id(UUID.randomUUID().toString())
        .status(HttpStatus.NOT_FOUND.toString())
        .name("NOT_FOUND")
        .message(ex.getMessage())
        .build();
    log.error(ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(value = {AuthenticationException.class})
  protected ResponseEntity<?> handleUserCurrentStatusException(
      InternalAuthenticationServiceException ex
  ) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .id(UUID.randomUUID().toString())
        .status(HttpStatus.UNAUTHORIZED.toString())
        .name("UNAUTHORIZED")
        .message(ex.getMessage())
        .build();
    log.error(ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(value = {AccessDeniedException.class})
  protected ResponseEntity<?> handleAccessDeniedException(
      AccessDeniedException ex
  ) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .id(UUID.randomUUID().toString())
        .status(HttpStatus.FORBIDDEN.toString())
        .name("FORBIDDEN")
        .message(ex.getMessage())
        .build();
    log.error(ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(value = {IllegalArgumentException.class, VerificationTokenFailedException.class})
  protected ResponseEntity<?> handleIllegalArgumentException(
      RuntimeException ex
  ) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .id(UUID.randomUUID().toString())
        .status(HttpStatus.BAD_REQUEST.toString())
        .name("BAD_REQUEST")
        .message(ex.getMessage())
        .build();
    log.error(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  private ResponseEntity<?> getBadRequestErrorResponse(List<ErrorDetails> details) {
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .id(UUID.randomUUID().toString())
            .message("Invalid Parameter Provided")
            .name("VALIDATION_ERROR")
            .status(HttpStatus.BAD_REQUEST.toString())
            .details(details)
            .build();
    return ResponseEntity.badRequest().body(errorResponse);
  }

}
