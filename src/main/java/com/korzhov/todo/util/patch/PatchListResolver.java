package com.korzhov.todo.util.patch;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class PatchListResolver {

  public static <T, E extends Patchable> List<T> patchDtoList(
      Map<Long, PatchContainer<E>> idToContainer, PatchFunction<T, E> patchFunction) {
    List<T> patchedList = new ArrayList<>();

    for (var obj : idToContainer.entrySet()) {
      try {
        T patchedObject = patchFunction.apply(obj.getValue());
        patchedList.add(patchedObject);
      } catch (IllegalArgumentException ex) {
        log.error("Failed to PATCH specified entity with ID = {}", obj.getKey(), ex);
        throw new IllegalArgumentException(String.format(
            "Failed to PATCH specfied entity with ID = %s, %s", obj.getKey(), ex.getMessage()), ex);
      }
    }
    return patchedList;
  }

}
