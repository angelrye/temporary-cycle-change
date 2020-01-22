package com.ryan.temporarycyclechange.service.resource.mail;

import java.io.ByteArrayOutputStream;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import com.ryan.temporarycyclechange.exception.ReportGenerationException;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 
 * @author rsapl00
 */
public class ExcelMailOutputStreamImpl extends MailOutputStream {

    private final Logger logger = LoggerFactory.getLogger(ExcelMailOutputStreamImpl.class);

    public ExcelMailOutputStreamImpl(Object source) {
        super(source);
    }

    @Override
    public DataSource getMessageOutputStream() {
        Assert.notNull(source, "Attachment should not be empty.");

        if (!(source instanceof Workbook)) {
            throw new ReportGenerationException("Attachment is not an excel file.");
        }

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("About to write into output stream.");
                }

                ((Workbook) source).write(outputStream);

                if (logger.isDebugEnabled()) {
                    logger.debug("Successfull in writing excel into file.");
                }

                return new ByteArrayDataSource(outputStream.toByteArray(), "application/vnd.ms-excel");
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.error("ERROR in retrieving attachment.", e);
            }

            throw new ReportGenerationException("There is an issue while retrieving message attachment.", e);
        }
    }

}