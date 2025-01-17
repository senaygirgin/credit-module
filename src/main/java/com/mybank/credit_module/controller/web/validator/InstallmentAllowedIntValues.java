package com.mybank.credit_module.controller.web.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = InstallmentValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface InstallmentAllowedIntValues {
    String message() default "Number of installments can only be 6, 9, 12, 24 ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
