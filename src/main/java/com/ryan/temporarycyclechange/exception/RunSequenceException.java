package com.ryan.temporarycyclechange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author rsapl00
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class RunSequenceException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private static final String RUNSEQUENCE_ERROR = "Run sequence number invalid for cycle change request. ";

    public RunSequenceException(String message) {
        super(RUNSEQUENCE_ERROR + message);
    }

    public RunSequenceException() {
        super(RUNSEQUENCE_ERROR);
    }

}
