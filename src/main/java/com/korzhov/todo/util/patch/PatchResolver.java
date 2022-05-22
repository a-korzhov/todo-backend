package com.korzhov.todo.util.patch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flipkart.zjsonpatch.JsonPatch;
import com.flipkart.zjsonpatch.JsonPatchApplicationException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
public class PatchResolver<T> {

  T dto;

  ObjectMapper mapper;

  public <E extends Patchable> T patch(PatchContainer<E> container, Class<T> dtoType) {
    JsonNode targetNode = mapper.convertValue(dto, JsonNode.class);

    ArrayNode arrayNode = mapper.createArrayNode();

    JsonNode root = mapper.createObjectNode()
        .put("op", container.getOperation())
        .put("path", "/" + container.getFieldNameToPatch())
        .set("value", mapper.valueToTree(container.getValue()));

    arrayNode.add(root);

    JsonNode patchedJson;

    try {
      patchedJson = JsonPatch.apply(arrayNode, targetNode);
    } catch (JsonPatchApplicationException ex) {
      throw new IllegalArgumentException(
          String.format("Field '%s' is not supported or missing", container.getFieldNameToPatch()), ex);
    }
    try {
      return mapper.treeToValue(patchedJson, dtoType);
    } catch (JsonProcessingException ex) {
      throw new IllegalArgumentException(String.format(
          "Failed to bind patched JSON for field value = '%s'", container.getValue()), ex);
    }
  }

}
