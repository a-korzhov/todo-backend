package com.korzhov.todo.exception;

public class VerificationTokenFailedException extends RuntimeException {

  public VerificationTokenFailedException(String message) {
    super(message);
  }

  public VerificationTokenFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
