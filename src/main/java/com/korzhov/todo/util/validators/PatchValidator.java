package com.korzhov.todo.util.validators;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.InvalidJsonPatchException;
import com.flipkart.zjsonpatch.JsonPatch;
import com.korzhov.todo.util.validators.annotation.PatchConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PatchValidator implements ConstraintValidator<PatchConstraint, JsonNode> {
    @Override
    public boolean isValid(JsonNode patchDocument, ConstraintValidatorContext context) {
        try {
            JsonPatch.validate(patchDocument);
            return true;
        } catch (InvalidJsonPatchException ex) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ex.getMessage()).addConstraintViolation();
            return false;
        }
    }
}
