package com.korzhov.todo.enumeration.user;

import static com.korzhov.todo.util.EnumUtils.buildEnumErrorMessage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public enum UserStatusEnum {

  ACTIVE("active"),
  INACTIVE("inactive"),
  BLOCKED("blocked");

  private final String value;

  public static UserStatusEnum fromString(String text) {
    for (UserStatusEnum type : UserStatusEnum.values()) {
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
