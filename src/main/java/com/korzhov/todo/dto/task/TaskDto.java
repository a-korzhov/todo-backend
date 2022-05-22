package com.korzhov.todo.dto.task;

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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Value
@With
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Jacksonized
public class TaskDto {

  Long id;

  @NotBlank
  String title;

  @NotEmpty
  String priority;

  @NotEmpty
  String status;

  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  OffsetDateTime createdAt;

  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  OffsetDateTime updatedAt;
}
