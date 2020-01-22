package com.ryan.temporarycyclechange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author rsapl00
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class MaximumRunSchedulePerRunDateException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public MaximumRunSchedulePerRunDateException(String message) {
        super(message);
    }    

}