package com.ryan.temporarycyclechange.service.resource.mail;

import java.sql.Date;

import com.ryan.temporarycyclechange.domain.CycleChangeRequest;
import com.ryan.temporarycyclechange.domain.EmailDistribution;
import com.ryan.temporarycyclechange.service.resource.mail.EmailTypeEnum;

import javax.servlet.http.HttpServletRequest;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author rsapl00
 */
@Data
public class EmailDetails {

    private List<String> divisionIds;
    private List<String> recipientLdapIds = new ArrayList<>();
    
    @NonNull
    private Date startDate;
    
    @NonNull
    private Date endDate;
    
    private HttpServletRequest httpRequest;
    private EmailTypeEnum mailType;
    private String userDivision;
    private List<EmailDistribution> emailDistributions = new ArrayList<>();

    private List<CycleChangeRequest> cycleChangeRequests;

    public EmailDetails(List<String> divisionIds, Date startDate, Date endDate, HttpServletRequest httpRequest,
            EmailTypeEnum mailType) {

        this(httpRequest, mailType);
        this.divisionIds = divisionIds;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public EmailDetails(String ldapId, HttpServletRequest httpRequest, EmailTypeEnum mailType, List<String> divisionIds) {
        this(httpRequest, mailType);
        this.recipientLdapIds.addAll(Arrays.asList(ldapId));
        this.divisionIds = divisionIds.stream().distinct().collect(Collectors.toList());
    }

    public EmailDetails(HttpServletRequest httpRequest, EmailTypeEnum mailType) {
        this.httpRequest = httpRequest;
        this.mailType = mailType;
    }

    public EmailDetails(HttpServletRequest httpRequest, EmailTypeEnum mailType,
            List<EmailDistribution> emailDistributions, List<String> divisionIds) {
        this(httpRequest, mailType);
        this.emailDistributions.addAll(emailDistributions);
        this.divisionIds = divisionIds.stream().distinct().collect(Collectors.toList());
    }

    public EmailDetails(HttpServletRequest httpRequest, EmailTypeEnum mailType,
            List<EmailDistribution> emailDistributions, List<String> divisionIds, List<CycleChangeRequest> cycleChangeRequests) {
        this(httpRequest, mailType, emailDistributions, divisionIds);
        this.cycleChangeRequests = cycleChangeRequests;
    }

    public EmailDetails(HttpServletRequest httpRequest, EmailTypeEnum mailType,
            List<EmailDistribution> emailDistributions) {
        this(httpRequest, mailType);
        this.emailDistributions.addAll(emailDistributions);
    }

    public EmailDetails(CycleChangeRequest cycleChangeRequest, HttpServletRequest httpRequest, EmailTypeEnum mailType) {
        this(httpRequest, mailType);
        this.recipientLdapIds.addAll(Arrays.asList(cycleChangeRequest.getCreateUserId()));
    }

    public EmailDetails(List<CycleChangeRequest> cycleChangeRequests, HttpServletRequest httpRequest,
            EmailTypeEnum mailType) {

        this(httpRequest, mailType);
        this.cycleChangeRequests = cycleChangeRequests;
        this.recipientLdapIds.addAll(cycleChangeRequests.stream().map(c -> c.getCreateUserId()).distinct().collect(Collectors.toList()));
    }

}