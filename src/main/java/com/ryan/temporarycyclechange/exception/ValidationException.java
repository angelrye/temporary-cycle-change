package com.ryan.temporarycyclechange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author rsapl00
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private static final String RUNSEQUENCE_ERROR = "Validation failed for the cycle change request. ";

    public ValidationException(String message) {
        super(RUNSEQUENCE_ERROR + message);
    }

    public ValidationException() {
        super(RUNSEQUENCE_ERROR);
    }

}
