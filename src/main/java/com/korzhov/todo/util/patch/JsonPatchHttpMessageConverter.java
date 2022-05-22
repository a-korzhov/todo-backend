package com.korzhov.todo.util.patch;

import static com.korzhov.todo.util.patch.JsonPatchParser.parsePatchRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

/*
  Convert PATCH request to the Map with entity ID key and PatchContainer value.
 */
//@Component
public class JsonPatchHttpMessageConverter
    extends AbstractHttpMessageConverter<Map<Long, PatchContainer<Patchable>>> {

  private final ObjectMapper objectMapper;
  private final Validator validator;

  public JsonPatchHttpMessageConverter(ObjectMapper objectMapper, Validator validator) {
    super(MediaType.valueOf(PatchConstants.PATCH_MEDIA_TYPE));
    this.objectMapper = objectMapper;
    this.validator = validator;
  }

  @Override
  protected boolean supports(@NonNull Class<?> clazz) {
    return String.class.isAssignableFrom(clazz);
  }

  @Override
  @NonNull
  protected Map<Long, PatchContainer<Patchable>> readInternal(
      @NonNull Class<? extends Map<Long, PatchContainer<Patchable>>> clazz,
      @NonNull HttpInputMessage inputMessage
  ) throws IOException, HttpMessageNotReadableException {
    try {
      JsonNode patchRequest = objectMapper.readTree(inputMessage.getBody());
      Set<ConstraintViolation<JsonNode>> violations = validator.validate(patchRequest);
      if(!violations.isEmpty()){
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<JsonNode> constraintViolation : violations) {
          sb.append(constraintViolation.getMessage());
        }
        throw new ConstraintViolationException("Error occurred: " + sb, violations);
      }

      return parsePatchRequest(patchRequest);
    } catch (Exception e) {
      throw new HttpMessageNotReadableException(e.getMessage(), inputMessage);
    }
  }

  @Override
  protected void writeInternal(
      @NonNull Map<Long, PatchContainer<Patchable>> longPatchContainerMap,
      HttpOutputMessage outputMessage
  ) throws IOException, HttpMessageNotWritableException {
    objectMapper.writeValue(outputMessage.getBody(), longPatchContainerMap);
  }
}
