package com.korzhov.todo.service;

import static com.korzhov.todo.util.patch.JsonPatchParser.parsePatchRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.korzhov.todo.dto.user.UserPrincipal;
import com.korzhov.todo.dao.entity.Task;
import com.korzhov.todo.dao.repository.TaskRepository;
import com.korzhov.todo.dto.task.request.CreateTaskRequest;
import com.korzhov.todo.dto.task.TaskDto;
import com.korzhov.todo.dto.task.request.TaskPatchRequest;
import com.korzhov.todo.exception.ResourceNotFoundException;
import com.korzhov.todo.mapper.TaskMapper;
import com.korzhov.todo.util.patch.PatchContainer;
import com.korzhov.todo.util.patch.PatchListResolver;
import com.korzhov.todo.util.patch.PatchResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

  private final TaskRepository taskRepository;
  private final TaskMapper taskMapper;
  private final UserService userService;

  private final ObjectMapper mapper;

  @Transactional(readOnly = true)
  public TaskDto getTaskById(long id) {
    log.info("Getting task with ID = {}", id);
    Task task = taskRepository.findById(id).orElseThrow(
        () -> new ResourceNotFoundException("Task not found")
    );
    return taskMapper.toDto(task);
  }

  @Transactional(readOnly = true)
  public List<TaskDto> getTasks(UserPrincipal userPrincipal) {
    log.info("Getting tasks for user with id = {}", userPrincipal.getId());
    List<Task> tasks = taskRepository.findAllByUserId(userPrincipal.getId());
    return tasks.stream()
        .map(taskMapper::toDto)
        .collect(Collectors.toList());
  }

  @Transactional
  public TaskDto createTask(CreateTaskRequest request, UserPrincipal userPrincipal) {
    log.info("Creating task... by user with ID = {}", userPrincipal.getId());
    Task task = taskMapper.toEntity(request);
    task.setUser(userService.getUserByEmail(userPrincipal.getEmail()));
    Task savedTask = taskRepository.save(task);
    return taskMapper.toDto(savedTask);
  }

  public boolean deleteTaskById(long id) {
    log.info("Deleting task with ID = {}", id);
    try {
      taskRepository.deleteById(id);
      return true;
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }

  @Transactional
  public List<TaskDto> patchTaskList(JsonNode patchRequestBody) {
    Map<Long, PatchContainer<Task>> idToTaskContainerMap = parsePatchRequest(patchRequestBody);

    Map<Long, Task> idToTaskMap =
        taskRepository.findAllById(idToTaskContainerMap.keySet()).stream()
            .collect(Collectors.toMap(Task::getId, Function.identity()));

    idToTaskContainerMap.entrySet().removeIf(entry ->
        !idToTaskMap.containsKey(entry.getKey()));

    idToTaskContainerMap.forEach((id, container) ->
        container.setEntity(idToTaskMap.get(id)));

    return PatchListResolver
        .patchDtoList(idToTaskContainerMap, this::patchTask)
        .stream()
        .filter(Objects::nonNull)
        .map(taskMapper::toDto)
        .collect(Collectors.toList());
  }

  private Task patchTask(PatchContainer<Task> container) {
    Task task = container.getEntity();

    TaskPatchRequest patchedTaskDto = new PatchResolver<>(taskMapper.toPatchDto(task), mapper)
        .patch(container, TaskPatchRequest.class);

    taskMapper.patchFields(patchedTaskDto, task);

    return taskRepository.save(task);
  }
}
