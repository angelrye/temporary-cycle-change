package com.ryan.temporarycyclechange.validation;

import java.sql.Date;
import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 
 * @author rsapl00
 */
public class RundateValidator implements ConstraintValidator<RundateConstraint, Date> {

    @Override
    public void initialize(RundateConstraint constraintAnnotation) {}
    
    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {
        
        LocalDate date = value.toLocalDate();
        LocalDate currentDate = LocalDate.now();

        if (date.isAfter(currentDate.plusDays(7))) {
            return true;
        }

        return false;
    }


}
