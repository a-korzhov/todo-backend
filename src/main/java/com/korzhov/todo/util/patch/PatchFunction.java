package com.korzhov.todo.util.patch;

@FunctionalInterface
public interface PatchFunction<T, E extends Patchable> {

  T apply(PatchContainer<E> container);

}
