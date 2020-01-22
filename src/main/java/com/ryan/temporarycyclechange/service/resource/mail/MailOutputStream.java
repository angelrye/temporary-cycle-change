package com.ryan.temporarycyclechange.service.resource.mail;

import javax.activation.DataSource;

/**
 * 
 * @author rsapl00
 */
public abstract class MailOutputStream {

    protected Object source;

    public MailOutputStream(Object source) {
        this.source = source;
    }

    public abstract DataSource getMessageOutputStream();
}