package com.ryan.temporarycyclechange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author rsapl00
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class HostPosDatabaseEntryCorruptedException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HostPosDatabaseEntryCorruptedException(String message) {
        super(message);
    }

}