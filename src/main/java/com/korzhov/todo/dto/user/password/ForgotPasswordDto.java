package com.korzhov.todo.dto.user.password;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import com.korzhov.todo.util.serializer.DefaultInstantDeserializer;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;
import javax.validation.constraints.Email;

@Value
@Builder
@Jacksonized
public class ForgotPasswordDto {

  @Email(message = "Email is not valid")
  String email;

  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @JsonDeserialize(using = DefaultInstantDeserializer.class)
  OffsetDateTime requestTime;

}
