package com.korzhov.todo.dto.user.request;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
@Builder
@Jacksonized
public class UserLoginRequest {

  @NotBlank
  @Email(message = "Email is not valid.")
  String email;

  @NotBlank
  String password;

  Boolean rememberMe;
}
