package com.korzhov.todo.enumeration.file;

import static com.korzhov.todo.util.EnumUtils.buildEnumErrorMessage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public enum ImageContentType {

  JPG("image/jpeg", "jpg"),
  PNG("image/png", "png"),
  WEBP("image/webp", "webp");

  private final String contentType;
  private final String fileFormat;

  public static ImageContentType fromContentType(String text) {
    for (ImageContentType type : ImageContentType.values()) {
      if (type.getContentType().equalsIgnoreCase(text)) {
        return type;
      }
    }
    throw new IllegalArgumentException(buildEnumErrorMessage(text));
  }

}
