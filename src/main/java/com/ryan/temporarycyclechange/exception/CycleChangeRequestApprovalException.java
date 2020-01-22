package com.ryan.temporarycyclechange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author rsapl00
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class CycleChangeRequestApprovalException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CycleChangeRequestApprovalException(String message) {
        super(message);
    }

}