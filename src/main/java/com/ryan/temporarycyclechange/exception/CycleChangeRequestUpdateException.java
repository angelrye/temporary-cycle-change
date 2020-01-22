package com.ryan.temporarycyclechange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author rsapl00
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CycleChangeRequestUpdateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CycleChangeRequestUpdateException(String message) {
        super(message);
    }

    public CycleChangeRequestUpdateException() {
        super("Error in updating the selected cycle change schedule.");
    }

}
