package com.ryan.temporarycyclechange.validation;

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
@Constraint(validatedBy = CycleChangeRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CycleChangeRequestConstraint {

    String message() default "Invalid Cycle Change Request. Make sure the dates are in chronological order.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {}; 
}