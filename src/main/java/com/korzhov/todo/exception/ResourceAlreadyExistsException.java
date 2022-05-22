package com.korzhov.todo.exception;

import javax.validation.ValidationException;

public class ResourceAlreadyExistsException extends ValidationException {

  public ResourceAlreadyExistsException() {
  }

  public ResourceAlreadyExistsException(String message, Object... args) {
    super(String.format(message, args));
  }

  public ResourceAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}
