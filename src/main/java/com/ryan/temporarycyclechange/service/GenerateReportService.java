package com.ryan.temporarycyclechange.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.ryan.temporarycyclechange.controller.resource.GenerateReportDTO;
import com.ryan.temporarycyclechange.domain.CycleChangeRequest;
import com.ryan.temporarycyclechange.exception.CycleChangeNotFoundException;
import com.ryan.temporarycyclechange.exception.ReportGenerationException;
import com.ryan.temporarycyclechange.repository.GenerateReportRepository;
import com.ryan.temporarycyclechange.security.userdetails.User;
import com.ryan.temporarycyclechange.service.report.GenReportData;
import com.ryan.temporarycyclechange.service.resource.MailMessage;
import com.ryan.temporarycyclechange.service.resource.mail.EmailDetails;
import com.ryan.temporarycyclechange.service.resource.mail.EmailTypeEnum;
import com.ryan.temporarycyclechange.util.DateUtil;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author jchin13, rsapl00
 */
@Service
@Transactional(readOnly = true)
public class GenerateReportService {

    final private GenerateReportRepository repository;
    final private MailNotificationService mailService;
    final private HttpServletRequest request;

    public GenerateReportService(GenerateReportRepository repository, MailNotificationService mailService,
            HttpServletRequest request) {
        this.repository = repository;
        this.mailService = mailService;
        this.request = request;
    }

    @Async
    public void generateReport(GenerateReportDTO dtoDates) {

        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<CycleChangeRequest> reportCycleChanges = null;
        if (user.isAdmin()) {
            reportCycleChanges = repository.findActiveByBetweenRunDatesAsc(dtoDates.getStartDateAsDate(),
            dtoDates.getEndDateAsDate(), DateUtil.getExpiryTimestamp());
        } else {
            reportCycleChanges = repository.findActiveByDivIdAndBetweenRunDatesAsc(user.getDivision(), dtoDates.getStartDateAsDate(),
            dtoDates.getEndDateAsDate(), DateUtil.getExpiryTimestamp());
        }

        if (reportCycleChanges == null || reportCycleChanges.isEmpty()) {
            throw new CycleChangeNotFoundException("No approved records found.");
        }

        try {
            Object[][] aRecords = new Object[reportCycleChanges.size() + 1][6];

            aRecords[0][0] = "DIVISION";
            aRecords[0][1] = "REQUEST TYPE";
            aRecords[0][2] = "RUN DAY";
            aRecords[0][3] = "RUN DATE";
            aRecords[0][4] = "EFFECTIVE DAY";
            aRecords[0][5] = "EFFECTIVE DATE";

            int x = 0;
            for (CycleChangeRequest aRecord : reportCycleChanges) {
                x++;
                // test display
                aRecords[x][0] = aRecord.getDivId();
                for (int y = 1; y <= 5; y++) {
                    if (y == 1) {
                        aRecords[x][y] = aRecord.getCycleChangeRequestType();
                    } else if (y == 2) {
                        aRecords[x][y] = aRecord.getRunDayName();
                    } else if (y == 3) {
                        aRecords[x][y] = aRecord.getRunDate().toString();
                    } else if (y == 4) {
                        aRecords[x][y] = aRecord.getEffectiveDayName();
                    } else if (y == 5) {
                        aRecords[x][y] = aRecord.getEffectiveDate().toString();
                    }
                }
            }

            GenReportData genHeaderMain = new GenReportData();
            Workbook workbook = genHeaderMain.generateData(aRecords);

            List<String> affectedDivs = reportCycleChanges.stream().map(rec -> rec.getDivId()).distinct()
                    .collect(Collectors.toList());

            MailMessage mailMessage = null;
            if (dtoDates.getSendToSelf()) {
                EmailDetails emailDetails = new EmailDetails(user.getUsername(), request,
                        EmailTypeEnum.EXCEL_REPORT_NOTIFICATION, affectedDivs);

                emailDetails.setStartDate(dtoDates.getStartDateAsDate());
                emailDetails.setEndDate(dtoDates.getEndDateAsDate());
                mailMessage = new MailMessage(emailDetails, workbook);
            } else {
                EmailDetails emailDetails = new EmailDetails(request, EmailTypeEnum.EXCEL_REPORT_NOTIFICATION,
                        mailService.getAdminAndUserDivEmailDistribution(affectedDivs), affectedDivs);
                emailDetails.setStartDate(dtoDates.getStartDateAsDate());
                emailDetails.setEndDate(dtoDates.getEndDateAsDate());

                mailMessage = new MailMessage(emailDetails, workbook);
            }

            StringBuffer fileName = new StringBuffer();
            fileName.append("Approved-Temp-Cycle-Change-").append(dtoDates.getStartDate()).append("-")
                    .append(dtoDates.getEndDate());
            mailMessage.setFileName(fileName.toString());

            mailService.sendNotification(mailMessage);

        } catch (Exception e) {
            throw new ReportGenerationException("An error has occurred while generating the report.", e);
        }
    }

}