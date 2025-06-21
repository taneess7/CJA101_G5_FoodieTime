package com.foodietime.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;


@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordPatternIfPresent {
    String message() default "密碼需包含大小寫字母、數字，長度 4~20 字元";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
