package com.korzhov.todo.controller;

import static com.korzhov.todo.util.patch.PatchConstants.PATCH_MEDIA_TYPE;

import com.fasterxml.jackson.databind.JsonNode;
import com.korzhov.todo.dto.user.UserPrincipal;
import com.korzhov.todo.dto.success.SuccessResponse;
import com.korzhov.todo.dto.task.request.CreateTaskRequest;
import com.korzhov.todo.dto.task.TaskDto;
import com.korzhov.todo.exception.ResourceNotFoundException;
import com.korzhov.todo.service.TaskService;
import com.korzhov.todo.util.validators.annotation.PatchConstraint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

  private final TaskService taskService;

  @GetMapping("/{id}")
  @PreAuthorize("hasPermission(#id, 'read')")
  public ResponseEntity<TaskDto> getTask(
      @PathVariable long id
  ) {
    TaskDto task = taskService.getTaskById(id);
    return ResponseEntity.ok(task);
  }

  @GetMapping
  @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<List<TaskDto>> getTasks(@AuthenticationPrincipal UserPrincipal userPrincipal) {
    List<TaskDto> tasks = taskService.getTasks(userPrincipal);
    return ResponseEntity.ok(tasks);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_USER')")
  public ResponseEntity<TaskDto> createTask(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestBody @Valid CreateTaskRequest request
  ) {
    TaskDto createdTask = taskService.createTask(request, userPrincipal);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(createdTask);
  }

  @PatchMapping(consumes = PATCH_MEDIA_TYPE)
  @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<List<TaskDto>> patchTaskList(
      @RequestBody @PatchConstraint JsonNode patchDocument
  ) {
    List<TaskDto> taskDtoList = taskService.patchTaskList(patchDocument);
    return ResponseEntity.ok(taskDtoList);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<?> deleteTaskById(@PathVariable long id) {
    boolean isDeleted = taskService.deleteTaskById(id);
    if (!isDeleted) {
      throw new ResourceNotFoundException("Task was not found");
    }
    return ResponseEntity.ok(SuccessResponse.builder()
        .message("Task deleted successfully").build());
  }

}
