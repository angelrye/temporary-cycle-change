package com.ryan.temporarycyclechange.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.ryan.temporarycyclechange.controller.resource.CycleChangeRequestDTO;

/**
 * 
 * @author rsapl00
 */
public class CycleChangeRequestValidator
        implements ConstraintValidator<CycleChangeRequestConstraint, CycleChangeRequestDTO> {

    // private ValidationFacade validationFacade;

    // public CycleChangeRequestValidator(ValidationFacade validationFacade) {
    //     this.validationFacade = validationFacade;
    // }

    public CycleChangeRequestValidator() {}

    @Override
    public void initialize(CycleChangeRequestConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(final CycleChangeRequestDTO cycleChangeDTO, ConstraintValidatorContext context) {
        try {
            // return validationFacade.isvalid(cycleChangeDTO, false);           
            return true;
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addConstraintViolation();

            return false;
        }
    }
}