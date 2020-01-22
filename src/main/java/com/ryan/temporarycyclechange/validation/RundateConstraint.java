package com.ryan.temporarycyclechange.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 
 * @author rsapl00
 */
@Documented
@Constraint(validatedBy = RundateValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RundateConstraint {
    String message() default "Run date should be 7 days greater than current date.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {}; 
}