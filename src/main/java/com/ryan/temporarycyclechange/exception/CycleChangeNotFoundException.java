package com.ryan.temporarycyclechange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author rsapl00
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CycleChangeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CycleChangeNotFoundException(String message) {
        super(message);
    }

    public CycleChangeNotFoundException() {
        super("Cycle Change Request not found.");
    }

}
