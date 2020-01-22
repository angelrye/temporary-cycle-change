package com.ryan.temporarycyclechange.service;

import static com.ryan.temporarycyclechange.util.CycleScheduleUtility.cloneCycleChangeRequest;
import static com.ryan.temporarycyclechange.util.CycleScheduleUtility.createNewCycleChangeRequest;
import static com.ryan.temporarycyclechange.util.CycleScheduleUtility.isBaseScheduleExists;
import static com.ryan.temporarycyclechange.util.DateUtil.expireNow;
import static com.ryan.temporarycyclechange.util.DateUtil.getExpiryTimestamp;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.ryan.temporarycyclechange.controller.resource.CycleChangeRequestDTO;
import com.ryan.temporarycyclechange.domain.CycleChangeRequest;
import com.ryan.temporarycyclechange.domain.CycleSchedule;
import com.ryan.temporarycyclechange.domain.EmailDistribution;
import com.ryan.temporarycyclechange.domain.enums.ChangeStatusEnum;
import com.ryan.temporarycyclechange.domain.enums.CycleChangeRequestTypeEnum;
import com.ryan.temporarycyclechange.domain.enums.RunSequenceEnum;
import com.ryan.temporarycyclechange.exception.CycleChangeNotFoundException;
import com.ryan.temporarycyclechange.exception.CycleChangeRequestApprovalException;
import com.ryan.temporarycyclechange.exception.CycleChangeRequestCancelException;
import com.ryan.temporarycyclechange.exception.CycleChangeRequestUpdateException;
import com.ryan.temporarycyclechange.exception.HostPosDatabaseEntryCorruptedException;
import com.ryan.temporarycyclechange.repository.CycleChangeRequestRepository;
import com.ryan.temporarycyclechange.repository.CycleScheduleRepository;
import com.ryan.temporarycyclechange.security.userdetails.User;
import com.ryan.temporarycyclechange.service.resource.MailMessage;
import com.ryan.temporarycyclechange.service.resource.mail.EmailDetails;
import com.ryan.temporarycyclechange.service.resource.mail.EmailTypeEnum;
import com.ryan.temporarycyclechange.util.CycleScheduleUtility;
import com.ryan.temporarycyclechange.util.DateUtil;
import com.ryan.temporarycyclechange.validation.Validator;
import com.ryan.temporarycyclechange.validation.batch.BatchSevenDayRuleValidator;
import com.ryan.temporarycyclechange.validation.batch.BatchValidationFacade;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author rsapl00
 */
@Service
@Transactional(readOnly = true)
public class CycleChangeRequestService {

    final private CycleChangeRequestRepository cycChangeReqRepository;
    final private CycleScheduleRepository cycleScheduleRepository;
    final private MailNotificationService mailService;
    final private HttpServletRequest request;
    final private BatchValidationFacade batchValidator;

    public CycleChangeRequestService(final CycleChangeRequestRepository repository,
            final CycleScheduleRepository cycleScheduleRepository, final MailNotificationService mailService,
            final HttpServletRequest request, final BatchValidationFacade batchValidator) {
        this.cycChangeReqRepository = repository;
        this.cycleScheduleRepository = cycleScheduleRepository;
        this.mailService = mailService;
        this.request = request;
        this.batchValidator = batchValidator;
    }

    /**
     * 
     * @param divId
     * @param startRunDate
     * @param endRunDate
     * @return
     */
    public List<CycleChangeRequest> findCycleChangeRequestByDivIdAndRunDate(final String divId, final Date startRunDate,
            final Date endRunDate) {
        return cycChangeReqRepository.findByDivIdAndRunDateBetweenOrderByRunDateAsc(divId, startRunDate, endRunDate);
    }

    // findActiveByDivIdAndBetweenRunDatesAsc
    public List<CycleChangeRequest> findActiveByDivIdAndBetweenRunDatesAsc(final String divId, final Date startRunDate,
            final Date endRunDate) {
        return cycChangeReqRepository.findActiveByDivIdAndBetweenRunDatesAsc(divId, startRunDate, endRunDate,
                DateUtil.getExpiryTimestamp());
    }

    /**
     * 
     * @param divId
     * @param startRunDate
     * @param endRunDate
     * @return
     */
    @Transactional(readOnly = false)
    public List<CycleChangeRequest> generateCycleChangeRequest(final String divId, final Date startRunDate,
            final Date endRunDate) {

        final List<CycleChangeRequest> cycleChangeRequests = findActiveByDivIdAndBetweenRunDatesAsc(divId, startRunDate,
                endRunDate);

        final List<CycleChangeRequest> newCycleChangeRequests = generateCycleChangeRequestBasedOnCycleSchedule(divId,
                startRunDate, endRunDate, cycleChangeRequests, false);

        saveCycleChangeRequests(newCycleChangeRequests);

        return findActiveByDivIdAndBetweenRunDatesAsc(divId, startRunDate, endRunDate);
    }

    @Transactional(readOnly = false)
    public List<CycleChangeRequest> saveCycleChangeRequests(final List<CycleChangeRequest> cycleChangeRequests) {
        return cycChangeReqRepository.saveAll(cycleChangeRequests);
    }

    /**
     * Generate Cycle Change Request base on the given date range. Skip generation
     * if date is already exists in the database.
     * 
     */
    public List<CycleChangeRequest> generateCycleChangeRequestBasedOnCycleSchedule(final String divId,
            final Date startRunDate, final Date endRunDate, final List<CycleChangeRequest> cycleChangeRequests,
            final Boolean isBaseScheduleGeneration) {

        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final List<CycleSchedule> defaultCycleSchedules = cycleScheduleRepository.findByDivIdOrderByDayNumAsc(divId);

        final List<CycleChangeRequest> newSchedules = new ArrayList<>();

        // Set the end date to end date + 1 so that the end date will be included in the
        // generation.
        final LocalDate afterEndRunDate = (endRunDate.toLocalDate()).plusDays(1);

        for (LocalDate date = startRunDate.toLocalDate(); date.isBefore(afterEndRunDate); date = date.plusDays(1)) {

            final LocalDate currentDateInLoop = date;

            defaultCycleSchedules.stream().forEach(cycleSchedule -> {

                final List<CycleChangeRequest> baseSchedules = CycleScheduleUtility
                        .generateCycleChangeRequest(cycleSchedule, currentDateInLoop, user);

                baseSchedules.stream().forEach(base -> {
                    if (isBaseScheduleGeneration) {
                        newSchedules.add(base);
                    } else {
                        if (!isBaseScheduleExists(base.getRunDate().toLocalDate(),
                                base.getEffectiveDate().toLocalDate(), cycleChangeRequests)) {
                            newSchedules.add(base);
                        }
                    }
                });
            });
        }

        return newSchedules;
    }

    public List<CycleChangeRequest> validateEntries(final List<Long> ids) {
        List<CycleChangeRequest> cycles = searchCycleChangesByIds(ids).stream().map(cycle -> {
            return validateEntry(cycle);
        }).collect(Collectors.toList());

        return cycles;
    }

    public CycleChangeRequest validateEntry(CycleChangeRequest request) {
        if (!CycleChangeRequestTypeEnum.CANCEL.isEquals(request.getCycleChangeRequestType())) {
            batchValidator.isvalid(CycleChangeRequestDTO.build(request), false);
        }

        return request;
    }

    public void validateSevenDayRule(final List<Long> ids) {
        Validator validator = new BatchSevenDayRuleValidator(cycChangeReqRepository, false);
        searchCycleChangesByIds(ids).stream().forEach(cycle -> {
            validator.isValid(CycleChangeRequestDTO.build(cycle));
        });
    }

    public CycleChangeRequest findById(final Long id) {
        return cycChangeReqRepository.findById(id)
                .orElseThrow(() -> new CycleChangeNotFoundException("Cycle Change not found."));
    }

    public List<CycleChangeRequest> findAll() {
        return cycChangeReqRepository.findAll();
    }

    @Transactional(readOnly = false)
    public CycleChangeRequest saveNewCycleChangeRequest(final CycleChangeRequest newCycleChange) {

        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final List<CycleChangeRequest> cycleChangeRequests = cycChangeReqRepository.findByDivIdAndRunDateAndNotExpired(
                newCycleChange.getDivId(), newCycleChange.getRunDate(), DateUtil.getExpiryTimestamp());

        final List<CycleChangeRequest> newCycleChangeRequests = new ArrayList<>();

        final List<CycleChangeRequest> filteredCycleChanges = cycleChangeRequests.stream().filter(cycle -> {
            if (!(ChangeStatusEnum.REJECTED.isEquals(cycle.getChangeStatusName())
                    || ChangeStatusEnum.CANCELLED.isEquals(cycle.getChangeStatusName()))) {

                if (!CycleChangeRequestTypeEnum.CANCEL.isEquals(cycle.getCycleChangeRequestType())) {
                    return true;
                }
            }

            return false;
        }).collect(Collectors.toList());

        if (filteredCycleChanges.isEmpty()) {
            newCycleChangeRequests.add(cycChangeReqRepository.save(
                    createNewCycleChangeRequest(newCycleChange, RunSequenceEnum.FIRST, CycleChangeRequestTypeEnum.ADD,
                            ChangeStatusEnum.getChangeStatusAfterAdminModification(user, ChangeStatusEnum.SAVED), user,
                            false)));
        } else {
            filteredCycleChanges.forEach(cycle -> {

                if (!(ChangeStatusEnum.REJECTED.isEquals(cycle.getChangeStatusName())
                        || ChangeStatusEnum.CANCELLED.isEquals(cycle.getChangeStatusName()))) {

                    if (CycleChangeRequestTypeEnum.CANCEL.isEquals(cycle.getCycleChangeRequestType())) {
                        return;
                    }

                    newCycleChangeRequests
                            .add(cycChangeReqRepository.save(createNewCycleChangeRequest(newCycleChange,
                                    RunSequenceEnum.SECOND, CycleChangeRequestTypeEnum.ADD, ChangeStatusEnum
                                            .getChangeStatusAfterAdminModification(user, ChangeStatusEnum.SAVED),
                                    user, false)));
                }
            });
        }

        if (newCycleChangeRequests.isEmpty()) {
            throw new HostPosDatabaseEntryCorruptedException("Data integrity issue. Please contact support.");
        }

        return newCycleChangeRequests.size() > 0 ? newCycleChangeRequests.get(0) : null;
    }

    @Transactional(readOnly = false)
    public CycleChangeRequest approveCycleChangeRequest(final Long id) {

        return cycChangeReqRepository.findById(id).map(existingCycleChange -> {
            validateEntry(existingCycleChange);

            final CycleChangeRequest newRequest = returnApprovedOrRejectCycleChange(ChangeStatusEnum.APPROVED,
                    existingCycleChange);

            final EmailDetails emailDetails = new EmailDetails(existingCycleChange, request,
                    EmailTypeEnum.RECORD_STATUS_APPROVED_NOTIFICATION);
            final MailMessage message = new MailMessage(emailDetails);

            mailService.sendNotification(message);

            return newRequest;
        }).orElseThrow(
                () -> new CycleChangeNotFoundException("Can't approve cycle change as the record doesn't exists."));
    }

    @Transactional(readOnly = false)
    public CycleChangeRequest rejectCycleChangeRequest(final Long id) {

        return cycChangeReqRepository.findById(id).map(forReject -> {
            final CycleChangeRequest newRequest = returnApprovedOrRejectCycleChange(ChangeStatusEnum.REJECTED,
                    forReject);

            final EmailDetails emailDetails = new EmailDetails(forReject, request,
                    EmailTypeEnum.RECORD_STATUS_REJECTED_NOTIFICATION);
            final MailMessage message = new MailMessage(emailDetails);

            mailService.sendNotification(message);

            return newRequest;
        }).orElseThrow(
                () -> new CycleChangeNotFoundException("Can't reject cycle change as the record doesn't exists."));
    }

    @Transactional(readOnly = false)
    public List<CycleChangeRequest> approveMultipleCycleChangeRequest(final List<Long> ids) {

        validateEntries(ids);

        final List<CycleChangeRequest> approvedChanges = searchCycleChangesByIds(ids).stream().map(cycle -> {
            final CycleChangeRequest newRequest = returnApprovedOrRejectCycleChange(ChangeStatusEnum.APPROVED, cycle);

            return newRequest;
        }).collect(Collectors.toList());

        List<String> affectedDivs = approvedChanges.stream().map(r -> r.getDivId()).distinct()
                .collect(Collectors.toList());
        List<EmailDistribution> emails = mailService.getAdminEmailDistribution();
        emails.addAll(mailService.getEmailDistributionByDivIds(affectedDivs));

        final EmailDetails emailDetails = new EmailDetails(request, EmailTypeEnum.RECORD_STATUS_APPROVED_NOTIFICATION,
                emails, affectedDivs, approvedChanges);

        final MailMessage message = new MailMessage(emailDetails);

        mailService.sendNotification(message);

        return approvedChanges;
    }

    @Transactional(readOnly = false)
    public List<CycleChangeRequest> rejectMultipleCycleChangeRequest(final List<Long> ids) {

        final List<CycleChangeRequest> rejectedChanges = searchCycleChangesByIds(ids).stream().map(cycle -> {
            final CycleChangeRequest newRequest = returnApprovedOrRejectCycleChange(ChangeStatusEnum.REJECTED, cycle);

            return newRequest;
        }).collect(Collectors.toList());

        List<String> affectedDivs = rejectedChanges.stream().map(r -> r.getDivId()).distinct()
                .collect(Collectors.toList());
        List<EmailDistribution> emails = mailService.getAdminEmailDistribution();
        emails.addAll(mailService.getEmailDistributionByDivIds(affectedDivs));

        final EmailDetails emailDetails = new EmailDetails(request, EmailTypeEnum.RECORD_STATUS_REJECTED_NOTIFICATION,
                emails, affectedDivs, rejectedChanges);

        final MailMessage message = new MailMessage(emailDetails);

        mailService.sendNotification(message);

        return rejectedChanges;
    }

    @Transactional(readOnly = false)
    public List<CycleChangeRequest> forApprovalCycleChangeRequest(final List<Long> ids) {

        validateEntries(ids);

        final List<CycleChangeRequest> forApprovals = searchCycleChangesByIds(ids).stream().map(cycle -> {
            return returnApprovedOrRejectCycleChange(ChangeStatusEnum.FOR_APPROVAL, cycle);

        }).collect(Collectors.toList());

        return forApprovals;
    }

    @Async
    public void sendForApprovalRequestViaEmail(final Date startRunDate, final Date endRunDate) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<CycleChangeRequest> cycles = null;
        if (user.isAdmin()) {
            cycles = cycChangeReqRepository.findActiveByBetweenRunDatesAsc(startRunDate, endRunDate,
                    DateUtil.getExpiryTimestamp());
        } else {
            cycles = cycChangeReqRepository.findActiveByDivIdAndBetweenRunDatesAsc(user.getDivision(), startRunDate,
                    endRunDate, DateUtil.getExpiryTimestamp());
        }

        final EmailDetails emailDetails = new EmailDetails(request,
                EmailTypeEnum.RECORD_STATUS_FORAPPROVAL_NOTIFICATION, mailService.getAdminEmailDistribution(),
                cycles.stream().map(r -> r.getDivId()).distinct().collect(Collectors.toList()), cycles);
        final MailMessage message = new MailMessage(emailDetails);

        mailService.sendNotification(message);
    }

    @Transactional(readOnly = false)
    public List<CycleChangeRequest> cancelCycleChangeRequest(final List<Long> ids) {

        return searchCycleChangesByIds(ids).stream().map(cycle -> {

            if (ChangeStatusEnum.REJECTED.isEquals(cycle.getChangeStatusName())
                    || ChangeStatusEnum.BASE.isEquals(cycle.getChangeStatusName())
                    || ChangeStatusEnum.CANCELLED.isEquals(cycle.getChangeStatusName())) {
                throw new CycleChangeRequestCancelException(
                        "Cancelation error: Only SAVED/FOR APPROVAL/APPROVED can be canceled.");
            }

            CycleChangeRequest canceled = cloneCycleChangeRequest(cycle);
            canceled.setId(0l);
            canceled.setChangeStatusName(ChangeStatusEnum.CANCELLED.getChangeStatus());

            canceled.setExpiryTimestamp(getExpiryTimestamp());
            canceled = cycChangeReqRepository.save(canceled);

            cycle.setExpiryTimestamp(expireNow());
            cycle.setComment("Referenced to new Cycle Change ID: " + canceled.getId());

            return canceled;
        }).collect(Collectors.toList());
    }

    private List<CycleChangeRequest> searchCycleChangesByIds(final List<Long> ids) {
        final List<CycleChangeRequest> existingCycleChanges = cycChangeReqRepository
                .findByIdInOrderByDivIdAscRunDateAsc(ids);

        if (existingCycleChanges.size() < ids.size()) {
            throw new CycleChangeNotFoundException("Record not found on one or more cycle change request.");
        }

        return existingCycleChanges;
    }

    private CycleChangeRequest returnApprovedOrRejectCycleChange(final ChangeStatusEnum changeType,
            final CycleChangeRequest existingCycleChange) {

        if (changeType == ChangeStatusEnum.FOR_APPROVAL) {
            if (!ChangeStatusEnum.SAVED.isEquals(existingCycleChange.getChangeStatusName())) {
                throw new CycleChangeRequestApprovalException(
                        "For Approval Error: Cycle Change should be in SAVED status.");
            }
        } else if ((changeType == ChangeStatusEnum.APPROVED || changeType == ChangeStatusEnum.REJECTED)) {

            if (changeType == ChangeStatusEnum.REJECTED
                    && (!ChangeStatusEnum.FOR_APPROVAL.isEquals(existingCycleChange.getChangeStatusName()))) {
                throw new CycleChangeRequestApprovalException(
                        "Rejection Error: Cycle Change should be in FOR APPROVAL status.");
            }

            if (!(ChangeStatusEnum.FOR_APPROVAL.isEquals(existingCycleChange.getChangeStatusName())
                    || ChangeStatusEnum.SAVED.isEquals(existingCycleChange.getChangeStatusName()))) {

                String message = "Approval";
                if (ChangeStatusEnum.REJECTED == changeType) {
                    message = "Rejection";
                }
                throw new CycleChangeRequestApprovalException(
                        message + " Error: Cycle Change should be in SAVED or FOR APPROVAL status.");
            }
        }

        CycleChangeRequest newApprovedCycle = cloneCycleChangeRequest(existingCycleChange);
        newApprovedCycle.setId(0l);
        newApprovedCycle.setChangeStatusName(changeType.getChangeStatus());

        newApprovedCycle.setExpiryTimestamp(getExpiryTimestamp());
        newApprovedCycle = cycChangeReqRepository.save(newApprovedCycle);

        existingCycleChange.setExpiryTimestamp(expireNow());
        existingCycleChange.setCycleChangeRequestType(CycleChangeRequestTypeEnum.BASE.getRequestType());
        existingCycleChange.setChangeStatusName(ChangeStatusEnum.BASE.getChangeStatus());
        existingCycleChange.setComment("Referenced to new Cycle Change ID: " + newApprovedCycle.getId());

        return newApprovedCycle;
    }

    @Transactional(readOnly = false)
    public CycleChangeRequest updateCycleChangeRequest(final CycleChangeRequest cycleChangeRequest) {

        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final List<CycleChangeRequest> existingCycles = cycChangeReqRepository.findByDivIdAndRunDateAndNotExpired(
                cycleChangeRequest.getDivId(), cycleChangeRequest.getRunDate(), DateUtil.getExpiryTimestamp());

        return cycChangeReqRepository.findById(cycleChangeRequest.getId()).map(toBeUpdatedCycle -> {

            if (ChangeStatusEnum.APPROVED.isEquals(toBeUpdatedCycle.getChangeStatusName())
                    || ChangeStatusEnum.REJECTED.isEquals(toBeUpdatedCycle.getChangeStatusName())
                    || ChangeStatusEnum.CANCELLED.isEquals(toBeUpdatedCycle.getChangeStatusName())) {
                throw new CycleChangeRequestUpdateException(
                        "You can only modify SAVED/FOR APPROVAL or Base Schedules.");
            }

            CycleChangeRequest newUpdateRequest = cloneCycleChangeRequest(toBeUpdatedCycle);
            newUpdateRequest.setId(0l); // remove id to create a new record
            newUpdateRequest.setRunDate(cycleChangeRequest.getRunDate());
            newUpdateRequest.setEffectiveDate(cycleChangeRequest.getEffectiveDate());
            newUpdateRequest.setOffsiteIndicator(cycleChangeRequest.getOffsiteIndicator());
            newUpdateRequest.setComment(cycleChangeRequest.getComment());

            if (DateUtil.isEqual(toBeUpdatedCycle.getEffectiveDate(), cycleChangeRequest.getEffectiveDate())
                    && toBeUpdatedCycle.getOffsiteIndicator().equals(cycleChangeRequest.getOffsiteIndicator())) {

                if (!toBeUpdatedCycle.getComment().equalsIgnoreCase(cycleChangeRequest.getComment())) {
                    newUpdateRequest = cycChangeReqRepository.save(createNewCycleChangeRequest(newUpdateRequest,
                            RunSequenceEnum.getRunSequenceEnum(toBeUpdatedCycle.getRunNumber()),
                            CycleChangeRequestTypeEnum
                                    .getChangeRequestTypeEnum(toBeUpdatedCycle.getCycleChangeRequestType()),
                            ChangeStatusEnum.getChangeStatusAfterAdminModification(user,
                                    toBeUpdatedCycle.getChangeStatusName()),
                            user, true));
                } else {
                    throw new CycleChangeRequestUpdateException("No changes detected on the selected schedule.");
                }

            } else {
                if (existingCycles.isEmpty()) {
                    newUpdateRequest = cycChangeReqRepository
                            .save(createNewCycleChangeRequest(newUpdateRequest, RunSequenceEnum.FIRST,
                                    CycleChangeRequestTypeEnum.MODIFY, ChangeStatusEnum
                                            .getChangeStatusAfterAdminModification(user, ChangeStatusEnum.SAVED),
                                    user, true));
                } else {
                    newUpdateRequest = cycChangeReqRepository
                            .save(createNewCycleChangeRequest(newUpdateRequest,
                                    RunSequenceEnum.getRunSequenceEnum(toBeUpdatedCycle.getRunNumber()),
                                    CycleChangeRequestTypeEnum.MODIFY, ChangeStatusEnum
                                            .getChangeStatusAfterAdminModification(user, ChangeStatusEnum.SAVED),
                                    user, true));
                }
            }

            toBeUpdatedCycle.setExpiryTimestamp(Timestamp.valueOf(LocalDateTime.now()));
            toBeUpdatedCycle.setCycleChangeRequestType(CycleChangeRequestTypeEnum.BASE.getRequestType());
            toBeUpdatedCycle.setChangeStatusName(ChangeStatusEnum.BASE.getChangeStatus());
            toBeUpdatedCycle.setComment("Referenced to new Cycle Change ID: " + newUpdateRequest.getId());

            return newUpdateRequest;

        }).orElseThrow(() -> new CycleChangeNotFoundException("Cycle Change not found."));

    }

    @Transactional(readOnly = false)
    public List<CycleChangeRequest> cancelCycleChangeRun(final List<Long> ids) {

        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        validateSevenDayRule(ids);

        return searchCycleChangesByIds(ids).stream().map(cycle -> {

            if (!ChangeStatusEnum.BASE.isEquals(cycle.getChangeStatusName())) {
                throw new CycleChangeRequestCancelException("You can only cancel base schedules.");
            }

            CycleChangeRequest canceled = cloneCycleChangeRequest(cycle);
            canceled.setId(0l);
            canceled.setCycleChangeRequestType(CycleChangeRequestTypeEnum.CANCEL.getRequestType());
            canceled.setChangeStatusName(ChangeStatusEnum
                    .getChangeStatusAfterAdminModification(user, ChangeStatusEnum.SAVED).getChangeStatus());
            canceled.setCreateUserId(user.getUsername());

            canceled.setExpiryTimestamp(getExpiryTimestamp());
            canceled = cycChangeReqRepository.save(canceled);

            cycle.setExpiryTimestamp(expireNow());
            cycle.setComment("Referenced to new Cycle Change ID: " + canceled.getId());

            return canceled;
        }).collect(Collectors.toList());
    }

    public List<CycleChangeRequest> getHistoryRecord(final String divId, final Date startRunDate,
            final Date endRunDate) {
        final List<CycleChangeRequest> history = cycChangeReqRepository.findHistoryDivIdAndBetweenRunDatesAsc(divId,
                startRunDate, endRunDate);

        if (history == null || history.isEmpty()) {
            throw new CycleChangeNotFoundException("Cycle Change not found.");
        }

        return history;
    }
}