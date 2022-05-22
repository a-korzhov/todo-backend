package com.korzhov.todo.util.validators.annotation;

import com.korzhov.todo.util.validators.PatchValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PatchValidator.class)
public @interface PatchConstraint {
    String message() default "Invalid JSON Patch payload (not an array)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
