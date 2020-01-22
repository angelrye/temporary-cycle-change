package com.ryan.temporarycyclechange.service;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeMessage;

import com.ryan.temporarycyclechange.domain.EmailDistribution;
import com.ryan.temporarycyclechange.repository.EmailDistributionRepository;
import com.ryan.temporarycyclechange.security.userdetails.User;
import com.ryan.temporarycyclechange.service.resource.MailMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * 
 * @author rsapl00
 */
@Service
public class MailNotificationService {

    @Value(value="${spring.mail.username}")
    private String emailFrom;

    private final String ADMIN_DIVISION_ID = "99";

    private final Log logger = LogFactory.getLog(MailNotificationService.class);

    private final JavaMailSender javaMailSender;
    private final EmailDistributionRepository emailDistributionRepository;
    
    public MailNotificationService(JavaMailSender javaMailSender,
            EmailDistributionRepository emailDistributionRepository) {
        this.javaMailSender = javaMailSender;
        this.emailDistributionRepository = emailDistributionRepository;
    }

    public List<EmailDistribution> getEmailDistributionByDivIds(final List<String> divIds) {
        return emailDistributionRepository.findByDivIds(divIds);
    }

    public List<EmailDistribution> getAdminEmailDistribution() {
        return emailDistributionRepository.getAdminEmailDistribution(ADMIN_DIVISION_ID);
    }

    public List<EmailDistribution> getAdminAndUserDivEmailDistribution(final List<String> divIds) {
        List<EmailDistribution> emails = new ArrayList<>();
        emails.addAll(getEmailDistributionByDivIds(divIds));
        emails.addAll(getAdminEmailDistribution());

        return emails;
    }

    @Async
    public void sendNotification(final MailMessage message) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {

            if (logger.isDebugEnabled()) {
                logger.debug("Sending simple message email notification.");
            }

            if (message.getAttachment() == null) {

                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setTo(message.getToRecipient().stream().toArray(String[]::new));
                mail.setCc(user.getEmail());
                mail.setSubject(message.getSubject());
                mail.setText(message.getMessageBody());
                mail.setFrom(emailFrom);
                javaMailSender.send(mail);

            } else {

                Assert.notNull(message.getAttachment(), "Empty attachment.");
                
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                
                helper.setTo(message.getToRecipient().stream().toArray(String[]::new));
                helper.setCc(user.getEmail());
                helper.setSubject(message.getSubject());
                helper.setText(message.getMessageBody());

                helper.setFrom(emailFrom);
                
                helper.addAttachment(message.getFileNameAndType(), message.getMessageAttachment());
                
                if (logger.isDebugEnabled()) {
                    logger.debug("Sending spreadsheet report via email.");
                }

                javaMailSender.send(mimeMessage);

                if (logger.isDebugEnabled()) {
                    logger.debug("Report sent successfully.");
                }
            }
        } catch (Exception me) {
            throw new MailSendException("There was an issue while sending email notification.", me);
        }
    }
}