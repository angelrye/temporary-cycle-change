package com.ryan.temporarycyclechange.service.resource.mail;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * 
 * @author rsapl00
 */
public final class MessageOutputStreamFactory {

    public static MailOutputStream getMessageOutputStream(Object source) {
        if (source instanceof Workbook) {
            return new ExcelMailOutputStreamImpl(source);
        }
        return null;
    }
}