package com.korzhov.todo.dto.success;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SuccessResponse<T> {

  String message;

  T data;

}
