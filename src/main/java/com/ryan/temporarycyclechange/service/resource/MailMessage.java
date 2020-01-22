package com.ryan.temporarycyclechange.service.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.activation.DataSource;

import com.ryan.temporarycyclechange.service.resource.mail.EmailDetails;
import com.ryan.temporarycyclechange.service.resource.mail.MailOutputStream;
import com.ryan.temporarycyclechange.service.resource.mail.MessageOutputStreamFactory;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author rsapl00
 */
@Data
@NoArgsConstructor
public class MailMessage {

    public static final String EMAIL_DOMAIN = "@safeway.com";

    private List<String> toRecipient = new ArrayList<>();

    @Value("${spring.mail.username}")
    private String fromRecipient;

    private String ccRecipient;

    private EmailDetails emailDetails;
    private String subject;
    private String messageBody;
    private Object attachment;

    private String fileName;
    private String fileType;

    public MailMessage(List<String> emailDistributionList) {
        this.toRecipient = emailDistributionList;
    }
    
    public MailMessage(EmailDetails emailDetails) {
        this.emailDetails = emailDetails;
        this.toRecipient.addAll(emailDetails.getRecipientLdapIds().stream().map(id -> id + EMAIL_DOMAIN).distinct().collect(Collectors.toList()));
        this.toRecipient.addAll(emailDetails.getEmailDistributions().stream().map(e -> e.getEmail()).distinct().collect(Collectors.toList()));
        
        setMessageSubjectAndBody(this.emailDetails);
    }

    public MailMessage(EmailDetails emailDetails, Object attachment) {
        this(emailDetails);
        this.attachment = attachment;
    }
    
    public DataSource getMessageAttachment() {
        MailOutputStream outputStream = MessageOutputStreamFactory.getMessageOutputStream(attachment);
        return outputStream.getMessageOutputStream();
    }
    
    public String getFileNameAndType() {
        return this.fileName + (this.attachment instanceof Workbook ? ".xlsx" : ".txt");
    }

    private void setMessageSubjectAndBody(EmailDetails emailDetails) {
        this.setSubject(emailDetails.getMailType().getMessageSubject().extract(emailDetails));
        this.setMessageBody(emailDetails.getMailType().getMessageBody().extract(emailDetails));
    }
}