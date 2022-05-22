package com.korzhov.todo.util.patch;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class PatchContainer<T extends Patchable> {

  String operation;

  Long id;

  String fieldNameToPatch;

  Object value;

  T entity;
}
