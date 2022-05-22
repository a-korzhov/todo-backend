package com.korzhov.todo.mapper;

import com.korzhov.todo.dto.user.request.UserCreateRequest;
import com.korzhov.todo.dao.entity.User;
import com.korzhov.todo.dto.user.UserDto;
import com.korzhov.todo.dto.user.request.UserPatchRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

  @Mapping(target = "imageData", ignore = true)
  @Mapping(target = "tasksCount", ignore = true)
  @Mapping(target = "tasksDoneCount", ignore = true)
  @Mapping(target = "tasksToDoCount", ignore = true)
  @Mapping(target = "jwtToken", ignore = true)
  public abstract UserDto toDto(User user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "imageName", ignore = true)
  @Mapping(target = "lastLoggedInTime", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "userVerification", ignore = true)
  public abstract User toEntity(UserCreateRequest request);

  public abstract UserPatchRequest toPatchDto(User user);

  @Mapping(target = "password", ignore = true)
  @Mapping(target = "imageName", ignore = true)
  @Mapping(target = "lastLoggedInTime", ignore = true)
  @Mapping(target = "taskList", ignore = true)
  public abstract void patchFields(UserPatchRequest request, @MappingTarget User user);
}
