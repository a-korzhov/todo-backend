package com.korzhov.todo.dto.user.request;

import com.korzhov.todo.util.validators.annotation.Password;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Value
@With
@Builder
@Jacksonized
public class UpdatePasswordRequest {

  String guid;

  @Password
  String password;

}
