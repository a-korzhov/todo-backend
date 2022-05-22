package com.korzhov.todo.util.patch;

import static com.korzhov.todo.util.patch.PatchConstants.OPERATION_FIELD;
import static com.korzhov.todo.util.patch.PatchConstants.PATCH_FIELD;
import static com.korzhov.todo.util.patch.PatchConstants.VALUE_FIELD;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class JsonPatchParser {

  private static final String SLASH = "/";
  private static final String EMPTY_STRING = "";

  private static final int PATH_ENTITY_ID_POSITION = 0;
  private static final int PATH_ENTITY_FIELD_NAME_POSITION = 1;

  private JsonPatchParser() {
  }

  public static <T extends Patchable> Map<Long, PatchContainer<T>> parsePatchRequest(
      JsonNode patchRequest
  ) {
    Map<Long, PatchContainer<T>> idToContainerMap = new HashMap<>();

    for (var patchObject : patchRequest) {
      String[] pathElements = patchObject.get(PATCH_FIELD).asText()
          .replaceFirst("^/", EMPTY_STRING)
          .split(SLASH);
      long parsedId = Long.parseLong(pathElements[PATH_ENTITY_ID_POSITION]);

      PatchContainer<T> container = PatchContainer.<T>builder()
          .operation(patchObject.get(OPERATION_FIELD).asText())
          .id(parsedId)
          .fieldNameToPatch(pathElements[PATH_ENTITY_FIELD_NAME_POSITION])
          .value(patchObject.get(VALUE_FIELD).asText())
          .build();
      idToContainerMap.put(parsedId, container);
    }
    return idToContainerMap;
  }
}
