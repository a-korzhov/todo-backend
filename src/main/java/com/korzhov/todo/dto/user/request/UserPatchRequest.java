package com.korzhov.todo.dto.user.request;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserPatchRequest {

  String phone;

}
