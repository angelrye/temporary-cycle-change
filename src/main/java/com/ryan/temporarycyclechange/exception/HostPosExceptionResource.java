package com.ryan.temporarycyclechange.exception;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author rsapl00
 */
public class HostPosExceptionResource {

    private Date timestamp;
    private List<String> messages;
    private String details;

    public HostPosExceptionResource(Date timestamp, List<String> messages, String details) {
        this.timestamp = timestamp;
        this.messages = messages;
        this.details = details;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return messages.stream().map(n -> n.toString()).collect(Collectors.joining(", "));
    }

    public String getDetails() {
        return details;
    }

}