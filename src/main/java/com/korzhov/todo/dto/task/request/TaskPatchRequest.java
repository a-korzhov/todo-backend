package com.korzhov.todo.dto.task.request;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class TaskPatchRequest {

  String title;

  String priority;

  String status;

}
