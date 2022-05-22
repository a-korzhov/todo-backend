package com.korzhov.todo.enumeration.task;

import static com.korzhov.todo.util.EnumUtils.buildEnumErrorMessage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public enum TaskPriority {
  MINOR("minor"),
  MAJOR("major"),
  CRITICAL("critical"),
  TRIVIAL("trivial");

  private final String value;

  public static TaskPriority fromString(String text) {
    for (TaskPriority type : TaskPriority.values()) {
      if (type.getValue().equalsIgnoreCase(text)) {
        return type;
      }
    }
    throw new IllegalArgumentException(buildEnumErrorMessage(text));
  }

  @Override
  public String toString() {
    return value;
  }
}
