package com.mybank.credit_module.controller.web.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InstallmentValidator implements ConstraintValidator<InstallmentAllowedIntValues, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if(InstallmentAllowedValues.contains(value))
            return true;
        return false;
    }
}
