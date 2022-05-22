package com.korzhov.todo.dto.user.request;

import com.korzhov.todo.util.validators.annotation.Password;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
@With
@Builder
@Jacksonized
public class UserCreateRequest {

  @NotBlank(message = "Phone must not be blank")
  String phone;

  @Password
  @NotBlank
  String password;

  @Email(message = "Email is not valid.")
  @NotBlank
  String email;

  @NotBlank
  String role;

}
