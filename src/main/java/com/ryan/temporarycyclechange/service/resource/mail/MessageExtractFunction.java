package com.ryan.temporarycyclechange.service.resource.mail;

/**
 * 
 * @author rsapl00
 */
@FunctionalInterface
public interface MessageExtractFunction {
    String extract(EmailDetails emailDetails);
}