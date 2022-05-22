package com.korzhov.todo.exception;

import org.springframework.security.authentication.AccountStatusException;

public class UserCurrentStatusException extends AccountStatusException {
  public UserCurrentStatusException(String msg) {
    super(msg);
  }

  public UserCurrentStatusException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
