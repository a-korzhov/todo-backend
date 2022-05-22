package com.korzhov.todo.util;

public class EnumUtils {

  private static final String ERROR_MESSAGE = "Unknown value=%s to convert";

  public static String buildEnumErrorMessage(String text) {
    return String.format(ERROR_MESSAGE, text);
  }
}
