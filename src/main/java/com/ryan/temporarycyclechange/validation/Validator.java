package com.ryan.temporarycyclechange.validation;

import com.ryan.temporarycyclechange.controller.resource.CycleChangeRequestDTO;
import com.ryan.temporarycyclechange.repository.CycleChangeRequestRepository;

/**
 * 
 * @author rsapl00
 */
public abstract class Validator {

    protected CycleChangeRequestRepository repository;
    protected Validator validator;

    public Validator() {}

    public Validator(CycleChangeRequestRepository repository) {
        this.repository = repository;
    }

    public Validator(CycleChangeRequestRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public abstract Boolean isValid(final CycleChangeRequestDTO dto);
}