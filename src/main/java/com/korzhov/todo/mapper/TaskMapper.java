package com.korzhov.todo.mapper;

import com.korzhov.todo.dao.entity.Task;
import com.korzhov.todo.dto.task.request.CreateTaskRequest;
import com.korzhov.todo.dto.task.TaskDto;
import com.korzhov.todo.dto.task.request.TaskPatchRequest;
import com.korzhov.todo.enumeration.task.TaskPriority;
import com.korzhov.todo.enumeration.task.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", imports = {TaskPriority.class, TaskStatus.class})
public abstract class TaskMapper {

  @Mapping(target = "priority", expression = "java(task.getPriority().toString())")
  @Mapping(target = "status", expression = "java(task.getStatus().toString())")
  public abstract TaskDto toDto(Task task);

  @Mapping(target = "priority", expression = "java(TaskPriority.fromString(taskDto.getPriority()))")
  @Mapping(target = "status", expression = "java(TaskStatus.fromString(taskDto.getStatus()))")
  public abstract Task toEntity(CreateTaskRequest taskDto);

  public abstract TaskPatchRequest toPatchDto(Task task);

  @Mapping(target = "priority", expression = "java(TaskPriority.fromString(request.getPriority()))")
  @Mapping(target = "status", expression = "java(TaskStatus.fromString(request.getStatus()))")
  public abstract void patchFields(TaskPatchRequest request, @MappingTarget Task task);

}
