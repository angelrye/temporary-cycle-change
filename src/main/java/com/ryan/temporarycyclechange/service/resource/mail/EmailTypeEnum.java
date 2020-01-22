package com.ryan.temporarycyclechange.service.resource.mail;

import java.util.stream.Collectors;

/**
 * 
 * @author rsapl00
 */
public enum EmailTypeEnum {

    RECORD_STATUS_BASE_NOTIFICATION((emailDetails) -> { return ""; }, (emailDetails) -> { return ""; }),

    RECORD_STATUS_APPROVED_NOTIFICATION(
        (emailDetails) -> {
            StringBuffer sb = new StringBuffer();
            sb.append("APPROVED: Host POS Cycle Change Request for Divisions (")
                .append(emailDetails.getDivisionIds().stream().distinct().collect(Collectors.joining(",")))
                .append(")");
            return sb.toString();
        }, (emailDetails) -> {
            return getMessageBody(emailDetails, "Your Cycle Change Request/s has/have been approved.", "view");
        }
    ),
    
    RECORD_STATUS_SAVED_NOTIFICATION(
        (emailDetails) -> {
            return new String("");
        }, (emailDetails) -> {
            return new String("");
        }
    ),

    RECORD_STATUS_FORAPPROVAL_NOTIFICATION(
        (emailDetails) -> {
            StringBuffer sb = new StringBuffer();
            sb.append("FOR APPROVAL: Host POS Cycle Change Request for Divisions (")
                .append(emailDetails.getDivisionIds().stream().distinct().collect(Collectors.joining(",")))
                .append(")");
            return sb.toString();
        }, (emailDetails) -> {
            return getMessageBody(emailDetails, "A cycle change request has been submitted and pending for your approval.", "approve");
        }
    ),
    
    RECORD_STATUS_REJECTED_NOTIFICATION(
        (emailDetails) -> {
            StringBuffer sb = new StringBuffer();
            sb.append("REJECTED: Host POS Cycle Change Request for Divisions (")
                .append(emailDetails.getDivisionIds().stream().distinct().collect(Collectors.joining(",")))
                .append(")");
            return sb.toString();
        }, (emailDetails) -> {
            return getMessageBody(emailDetails, "Your cycle change request has been rejected.", "view");
        }
    ),
    
    RECORD_STATUS_CANCELED_NOTIFICATION(
        (emailDetails) -> {
            return new String("");
        }, (emailDetails) -> {
            return new String("");
        }
    ),
    
    EXCEL_REPORT_NOTIFICATION(
        (emailDetails) -> {
            StringBuffer sb = new StringBuffer();
            sb.append("Notification: Temporary cycle change for ")
                .append(emailDetails.getStartDate())
                .append(" to ")
                .append(emailDetails.getEndDate());

            return sb.toString();
        }, (emailDetails) -> {
            StringBuffer sb = new StringBuffer();
            sb.append("Hi Everyone,")
                .append("\n\n\n")
                .append("This is to inform you that there will be a temporary cycle change for the following division (")
                .append(emailDetails.getDivisionIds().stream().collect(Collectors.joining(",")))
                .append(").")
                .append("\n\n")
                .append("Please refer to attached for the complete details.");

            return sb.toString();
    });

    private MessageExtractFunction messageSubject;
    private MessageExtractFunction messageBody;

    private EmailTypeEnum(MessageExtractFunction messageSubject, MessageExtractFunction messageBody) {
        this.messageSubject = messageSubject;
        this.messageBody = messageBody;
    }

    public MessageExtractFunction getMessageSubject() {
        return this.messageSubject;
    }

    public MessageExtractFunction getMessageBody() {
        return this.messageBody;
    }

    private static String getApprovalLink(EmailDetails emailDetails, String message, String action) {
        StringBuffer sb = new StringBuffer();
            sb.append(message)
            .append("\n\n\n")
            .append("Click here to ")
            .append(action + ". ")
            .append(emailDetails.getHttpRequest().getScheme())
            .append("://")
            .append(emailDetails.getHttpRequest().getServerName())
            .append(":")
            .append(emailDetails.getHttpRequest().getServerPort())
            .append(emailDetails.getHttpRequest().getContextPath());
        
        return sb.toString();
    }

    private static String getMessageBody(EmailDetails emailDetails, String message, String action) {
        final StringBuffer sb = new StringBuffer();
        sb.append("\n\n\n")
            .append(getApprovalLink(emailDetails, message, action))
            .append("\n\nDetails below: \n\n");

        sb.append("Division\t\tOffsite\t\tRun Date\t\tEffectivity Date\t\tCycle Change Request Type\t\tComment\n");

        emailDetails.getCycleChangeRequests().stream().forEach(cycle -> {
            sb.append(cycle.getDivId())
                .append("\t\t")
                .append(cycle.getOffsiteAsWordString())
                .append("\t\t")
                .append(cycle.getRunDate())
                .append("\t\t")
                .append(cycle.getEffectiveDate())
                .append("\t\t")
                .append(cycle.getCycleChangeRequestType())
                .append("\t\t")
                .append(cycle.getComment())
                .append("\n");
        });

        return sb.toString();
    }
}