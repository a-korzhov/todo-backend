package com.korzhov.todo.dto.task.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Value
@With
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Jacksonized
public class CreateTaskRequest {

  @NotBlank
  String title;

  @NotEmpty
  String priority;

  @NotEmpty
  String status;

}
