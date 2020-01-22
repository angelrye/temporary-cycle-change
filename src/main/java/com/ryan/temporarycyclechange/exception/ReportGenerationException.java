package com.ryan.temporarycyclechange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author rsapl00
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ReportGenerationException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ReportGenerationException(String message) {
        super(message);
    }    

    public ReportGenerationException(String message, Exception e) {
        super(message, e);
    }

}