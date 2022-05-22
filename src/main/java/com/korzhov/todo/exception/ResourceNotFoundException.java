package com.korzhov.todo.exception;

import javax.validation.ValidationException;

public class ResourceNotFoundException extends ValidationException {

  public ResourceNotFoundException() {
  }

  public ResourceNotFoundException(String message, Object ... args) {
    super(String.format(message, args));
  }

  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
