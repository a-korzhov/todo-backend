package com.korzhov.todo.enumeration.user;

import org.springframework.security.core.GrantedAuthority;

public enum UserRoleEnum implements GrantedAuthority {

  ROLE_USER,
  ROLE_ADMIN;

  @Override
  public String getAuthority() {
    return name();
  }

}
