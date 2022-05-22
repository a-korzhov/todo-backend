package com.korzhov.todo.enumeration.task;

import static com.korzhov.todo.util.EnumUtils.buildEnumErrorMessage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public enum TaskStatus {
  TODO("todo"),
  IN_PROGRESS("in progress"),
  DONE("done");

  private final String value;

  public static TaskStatus fromString(String text) {
    for (TaskStatus type : TaskStatus.values()) {
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
