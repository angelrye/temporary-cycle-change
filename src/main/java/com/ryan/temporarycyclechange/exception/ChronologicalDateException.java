package com.ryan.temporarycyclechange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author rsapl00
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ChronologicalDateException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private static final String CHRONOLOGICAL_ERROR = "Invalid Run or Effective Date. Dates should be in proper chronological order. ";

    public ChronologicalDateException(String message) {
        super(CHRONOLOGICAL_ERROR + message);
    }

    public ChronologicalDateException() {
        super("Dates should be in proper chronological order.");
    }

}
