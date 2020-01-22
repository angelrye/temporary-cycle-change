package com.ryan.temporarycyclechange.domain.enums;

import com.ryan.temporarycyclechange.security.userdetails.User;
import com.ryan.temporarycyclechange.service.resource.mail.EmailTypeEnum;

/**
 * 
 * @author rsapl00
 */
public enum ChangeStatusEnum {
    BASE("", "", "", EmailTypeEnum.RECORD_STATUS_BASE_NOTIFICATION),
    APPROVED("APPROVED", "APPROVED: Host POS Cycle Change Request",
            "Your Cycle Change Request/s has/have been approved.", EmailTypeEnum.RECORD_STATUS_APPROVED_NOTIFICATION),
    SAVED("SAVED", "", "", EmailTypeEnum.RECORD_STATUS_SAVED_NOTIFICATION),
    FOR_APPROVAL("FOR APPROVAL", "FOR APPROVAL: Host POS Cycle Change Request",
            "A cycle change request has been submitted and pending for your approval.",
            EmailTypeEnum.RECORD_STATUS_FORAPPROVAL_NOTIFICATION),
    REJECTED("REJECTED", "REJECTED: Host POS Cycle Change Request", "Your cycle change request has been rejected.",
            EmailTypeEnum.RECORD_STATUS_REJECTED_NOTIFICATION),
    CANCELLED("CANCELED", "", "", EmailTypeEnum.RECORD_STATUS_CANCELED_NOTIFICATION);

    private String status;
    private String mailSubject;
    private String mailMessageBody;
    private EmailTypeEnum mailType;

    private ChangeStatusEnum(final String status, String mailSubject, String mailMessageBody, EmailTypeEnum mailType) {
        this.status = status;
        this.mailSubject = mailSubject;
        this.mailMessageBody = mailMessageBody;
        this.mailType = mailType;
    }

    public String getChangeStatus() {
        return this.status;
    }

    public String getMailSubject() {
        return this.mailSubject;
    }

    public String getMailMessageBody() {
        return this.mailMessageBody;
    }

    public EmailTypeEnum getEmailType() {
        return this.mailType;
    }

    public boolean isEquals(String changeStatusName) {
        return this.getChangeStatus().equals(changeStatusName);
    }

    public static ChangeStatusEnum getChangeStatusEnum(final String status) {
        for (ChangeStatusEnum cStatus : ChangeStatusEnum.values()) {
            if (cStatus.getChangeStatus().equals(status)) {
                return cStatus;
            }
        }

        return ChangeStatusEnum.BASE;
    }

    public static ChangeStatusEnum getChangeStatusAfterAdminModification(User user, ChangeStatusEnum status) {
        if (user.isAdmin()) {
            return ChangeStatusEnum.FOR_APPROVAL;
        }

        return status;
    }

    public static ChangeStatusEnum getChangeStatusAfterAdminModification(User user, String changeStatus) {
        return getChangeStatusAfterAdminModification(user, getChangeStatusEnum(changeStatus));
    }
}