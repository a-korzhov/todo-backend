package com.korzhov.todo.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;

@Value
@With
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Jacksonized
public class UserDto {

  Long id;

  String email;

  String phone;

  String imageData;

  String role;

  String status;

  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  OffsetDateTime createdAt;

  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  OffsetDateTime updatedAt;

  int tasksCount;

  long tasksDoneCount;

  long tasksToDoCount;

  //Used to set it to the header in controller.
  @JsonIgnore
  String jwtToken;
}
