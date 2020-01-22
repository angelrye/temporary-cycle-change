package com.ryan.temporarycyclechange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author rsapl00
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateCycleChangeSchedule extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final String DUPLICATE_ERROR = "Duplicate cycle change schedule. ";

    public DuplicateCycleChangeSchedule(String message) {
        super(DUPLICATE_ERROR + message);
    }

    public DuplicateCycleChangeSchedule() {
        super(DUPLICATE_ERROR);
    }

}