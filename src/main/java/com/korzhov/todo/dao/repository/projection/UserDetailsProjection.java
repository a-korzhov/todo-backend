package com.korzhov.todo.dao.repository.projection;

import com.korzhov.todo.enumeration.user.UserRoleEnum;
import com.korzhov.todo.enumeration.user.UserStatusEnum;

public interface UserDetailsProjection {

  Long getId();

  String getPassword();

  String getEmail();

  UserRoleEnum getRole();

  UserStatusEnum getStatus();

  String getImageName();

}
