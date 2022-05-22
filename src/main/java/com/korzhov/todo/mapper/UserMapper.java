package com.korzhov.todo.mapper;

import com.korzhov.todo.dto.user.request.UserCreateRequest;
import com.korzhov.todo.dao.entity.User;
import com.korzhov.todo.dto.user.UserDto;
import com.korzhov.todo.dto.user.request.UserPatchRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

  public abstract UserDto toDto(User user);

  public abstract User toEntity(UserCreateRequest request);

  public abstract UserPatchRequest toPatchDto(User user);

  public abstract void patchFields(UserPatchRequest request, @MappingTarget User user);
}
