package com.ryan.temporarycyclechange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author rsapl00
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BufferDaysException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BufferDaysException(String message) {
        super(message);
    }

    public BufferDaysException() {
        super("You can only add or edit dates that are 7 days in advance.");
    }

}
