package com.ryan.temporarycyclechange.validation;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

/**
 * 
 * @author rsapl00
 */
public class ChronologicalOrderDateValidator implements ConstraintValidator<ChronologicalOrderDateConstraint, Object> {

    private String startDate;
    private String endDate;

    @Override
    public void initialize(ChronologicalOrderDateConstraint constraintAnnotation) {
        this.startDate = constraintAnnotation.startDate();
        this.endDate = constraintAnnotation.endDate();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        try {
            Object start = new BeanWrapperImpl(value).getPropertyValue(startDate);
            final LocalDate startDate = LocalDate.parse((CharSequence) start.toString());
            
            Object end = new BeanWrapperImpl(value).getPropertyValue(endDate);
            LocalDate endDate = LocalDate.parse((CharSequence) end.toString());

            return startDate.isBefore(endDate);
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid  date. Date should be in yyyy-MM-dd pattern.").addConstraintViolation();
            
            return false;
        }
    }
}