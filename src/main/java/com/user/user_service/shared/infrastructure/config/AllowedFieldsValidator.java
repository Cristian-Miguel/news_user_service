package com.user.user_service.shared.infrastructure.config;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AllowedFieldsValidator implements ConstraintValidator<AllowedFields, String>{

    private List<String> allowedFields;

    @Override
    public void initialize(AllowedFields constraintAnnotation) {
        this.allowedFields = Arrays.asList(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.isEmpty() || allowedFields.contains(value);
    }


}
