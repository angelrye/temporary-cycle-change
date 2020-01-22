package com.ryan.temporarycyclechange.util;

import static com.ryan.temporarycyclechange.util.DateUtil.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.ryan.temporarycyclechange.domain.CycleChangeRequest;
import com.ryan.temporarycyclechange.domain.CycleSchedule;
import com.ryan.temporarycyclechange.domain.enums.ChangeStatusEnum;
import com.ryan.temporarycyclechange.domain.enums.CorpEnum;
import com.ryan.temporarycyclechange.domain.enums.CycleChangeRequestTypeEnum;
import com.ryan.temporarycyclechange.domain.enums.DayEnum;
import com.ryan.temporarycyclechange.domain.enums.OffsiteIndicatorEnum;
import com.ryan.temporarycyclechange.domain.enums.RunSequenceEnum;
import com.ryan.temporarycyclechange.exception.InvalidEffectiveDate;
import com.ryan.temporarycyclechange.security.userdetails.User;
import com.ryan.temporarycyclechange.validation.ValidEffectiveDates;

/**
 * 
 * @author rsapl00
 */
public final class CycleScheduleUtility {

    public final static Integer MAXIMUM_SAME_EFFECTIVE_DATE = 2;

    public static boolean isBaseScheduleExists(final LocalDate runDate, final LocalDate effDate,
            final List<CycleChangeRequest> cycleChangeRequests) {

        return cycleChangeRequests.stream().anyMatch(change -> {
            return isBaseScheduleNotRejectedNorCanceledExists(runDate, effDate, change);
        });

    }

    private static boolean isBaseScheduleNotRejectedNorCanceledExists(final LocalDate runDate, final LocalDate effDate,
            final CycleChangeRequest cycleChangeRequest) {
        if (cycleChangeRequest.getRunDate().equals(java.sql.Date.valueOf(runDate))
                && cycleChangeRequest.getEffectiveDateBase().equals(java.sql.Date.valueOf(effDate))) {
            if (!(ChangeStatusEnum.REJECTED.isEquals(cycleChangeRequest.getChangeStatusName())
                    || ChangeStatusEnum.CANCELLED.isEquals(cycleChangeRequest.getChangeStatusName()))) {
                return true;
            }
        }

        return false;
    }

    public static List<CycleChangeRequest> removeCancelRequestFromRequests(
            final List<CycleChangeRequest> unfilteredList) {
        return unfilteredList.stream().filter(cycle -> {
            if (CycleChangeRequestTypeEnum.CANCEL.isEquals(cycle.getCycleChangeRequestType())) {
                return false;
            }

            return true;
        }).collect(Collectors.toList());
    }

    public static List<CycleChangeRequest> generateCycleChangeRequest(final CycleSchedule cycleSchedule,
            final LocalDate currentDateInLoop, User user) {

        final List<CycleChangeRequest> cycleChangeRequests = new ArrayList<>();

        // Compare by name: current date in loop to cycle schedule date
        if (DayEnum.getDayEnum(cycleSchedule.getDayNum())
                .equals(DayEnum.getDayEnum(currentDateInLoop.getDayOfWeek()))) {
            final DayEnum runDay = DayEnum.getDayEnum(cycleSchedule.getDayNum());

            final DayEnum defEffectiveDateOne = DayEnum.getDayEnum(cycleSchedule.getDefOneEffectiveDayNbr());
            final DayEnum defEffectiveDateTwo = DayEnum.getDayEnum(cycleSchedule.getDefTwoEffectiveDayNbr());

            final String offSiteOneInd = cycleSchedule.getDefaultRunOneOsInd();
            final String offSiteTwoInd = cycleSchedule.getDefaultRunTwoOsInd();

            // if Default Effective Date 1 and 2 is NOT 0 or has run date
            if (!defEffectiveDateOne.equals(DayEnum.NO_RUNDAY)) {
                cycleChangeRequests.add(createCycleChangeRequest(cycleSchedule, currentDateInLoop, runDay,
                        defEffectiveDateOne, offSiteOneInd, RunSequenceEnum.FIRST, user));

                if (!defEffectiveDateTwo.equals(DayEnum.NO_RUNDAY)) {
                    cycleChangeRequests.add(createCycleChangeRequest(cycleSchedule, currentDateInLoop, runDay,
                            defEffectiveDateTwo, offSiteTwoInd, RunSequenceEnum.SECOND, user));
                }
            }
        }

        return cycleChangeRequests;
    }

    public static CycleChangeRequest createNewCycleChangeRequest(final CycleChangeRequest submittedCycleChange,
            RunSequenceEnum runSequence, CycleChangeRequestTypeEnum requestType, ChangeStatusEnum status, User user,
            Boolean isUpdate) {

        Gson gson = new Gson();
        CycleChangeRequest newChangeRequest = gson.fromJson(gson.toJson(submittedCycleChange),
                CycleChangeRequest.class);

        newChangeRequest.setCorpId(CorpEnum.DEFAULT_CORP.getCorpId());

        if (!isUpdate) {
            newChangeRequest.setEffectiveDateBase(newChangeRequest.getEffectiveDate());
        }

        newChangeRequest.setRunNumber(runSequence.getRunSequence());
        newChangeRequest.setRunDayName(getDayName(newChangeRequest.getRunDate()));
        newChangeRequest.setEffectiveDayName(getDayName(newChangeRequest.getEffectiveDate()));

        if (newChangeRequest.getOffsiteIndicator().equals(OffsiteIndicatorEnum.NON_OFFSITE.getIndicator())) {
            if (requestType.equals(CycleChangeRequestTypeEnum.ADD)) {
                newChangeRequest.setCycleChangeRequestType(CycleChangeRequestTypeEnum.ADD.getRequestType());
            } else if (requestType.equals(CycleChangeRequestTypeEnum.MODIFY)) {
                newChangeRequest.setCycleChangeRequestType(CycleChangeRequestTypeEnum.MODIFY.getRequestType());
            }
        } else {
            if (requestType.equals(CycleChangeRequestTypeEnum.ADD)) {
                newChangeRequest.setCycleChangeRequestType(CycleChangeRequestTypeEnum.ADD_OFFSITE.getRequestType());
            } else if (requestType.equals(CycleChangeRequestTypeEnum.MODIFY)) {
                newChangeRequest.setCycleChangeRequestType(CycleChangeRequestTypeEnum.MODIFY_OFFSITE.getRequestType());
            }
        }

        newChangeRequest.setChangeStatusName(status.getChangeStatus());

        if (newChangeRequest.getCreateUserId() == null || newChangeRequest.getCreateUserId().isEmpty()) {
            newChangeRequest.setCreateUserId(user.getUsername());
        }

        newChangeRequest.setExpiryTimestamp(getExpiryTimestamp());

        return newChangeRequest;
    }

    public static CycleChangeRequest createNewCycleChangeRequest(final CycleChangeRequest submittedCycleChange,
            RunSequenceEnum runSequence, CycleChangeRequestTypeEnum requestType) {

        Gson gson = new Gson();
        CycleChangeRequest newChangeRequest = gson.fromJson(gson.toJson(submittedCycleChange),
                CycleChangeRequest.class);

        newChangeRequest.setCorpId(CorpEnum.DEFAULT_CORP.getCorpId());

        newChangeRequest.setRunNumber(runSequence.getRunSequence());
        newChangeRequest.setRunDayName(getDayName(newChangeRequest.getRunDate()));
        newChangeRequest.setEffectiveDayName(getDayName(newChangeRequest.getEffectiveDate()));

        if (newChangeRequest.getOffsiteIndicator().equals(OffsiteIndicatorEnum.NON_OFFSITE.getIndicator())) {
            if (requestType.equals(CycleChangeRequestTypeEnum.ADD)) {
                newChangeRequest.setCycleChangeRequestType(CycleChangeRequestTypeEnum.ADD.getRequestType());
            } else if (requestType.equals(CycleChangeRequestTypeEnum.MODIFY)) {
                newChangeRequest.setCycleChangeRequestType(CycleChangeRequestTypeEnum.MODIFY.getRequestType());
            }
        } else {
            if (requestType.equals(CycleChangeRequestTypeEnum.ADD)) {
                newChangeRequest.setCycleChangeRequestType(CycleChangeRequestTypeEnum.ADD_OFFSITE.getRequestType());
            } else if (requestType.equals(CycleChangeRequestTypeEnum.MODIFY)) {
                newChangeRequest.setCycleChangeRequestType(CycleChangeRequestTypeEnum.MODIFY_OFFSITE.getRequestType());
            }
        }

        newChangeRequest.setChangeStatusName(ChangeStatusEnum.SAVED.getChangeStatus());

        newChangeRequest.setExpiryTimestamp(getExpiryTimestamp());

        return newChangeRequest;
    }

    public static Boolean validateCycleChangeEffectiveDate(final List<CycleChangeRequest> beforeCycleChanges,
            final List<CycleChangeRequest> afterCycleChanges, final CycleChangeRequest submittedCycleChange) {

        ValidEffectiveDates before = processEffectiveDateFromEarlierEffectiveDates(beforeCycleChanges,
                submittedCycleChange);

        ValidEffectiveDates after = processEffectiveDateFromLaterEffectiveDates(afterCycleChanges,
                submittedCycleChange);

        boolean isBothValid = before.IsValid() && after.IsValid();

        int sameEffDtCount = before.getEffectiveDates().stream().filter(date -> {
            return isEqual(date, submittedCycleChange.getEffectiveDate());
        }).collect(Collectors.toList()).size() + after.getEffectiveDates().stream().filter(date -> {
            return isEqual(date, submittedCycleChange.getEffectiveDate());
        }).collect(Collectors.toList()).size();

        if (sameEffDtCount >= MAXIMUM_SAME_EFFECTIVE_DATE) {
            throw new InvalidEffectiveDate(
                    "Invalid effective date. Only two consecutive same effective date is valid.");
        }

        if (sameEffDtCount <= 1 && isBothValid) {
            return true;
        }

        return false;
    }

    private static ValidEffectiveDates processEffectiveDateFromLaterEffectiveDates(
            final List<CycleChangeRequest> ascOrderCycleChange, final CycleChangeRequest submittedCycleChange) {

        ValidEffectiveDates validEffDate = new ValidEffectiveDates();

        for (CycleChangeRequest cycle : ascOrderCycleChange) {

            // skip same run date since same run date has been processed in
            // processEffectiveDateFromEarlierEffectiveDates
            if (isEqual(cycle.getRunDate(), submittedCycleChange.getRunDate())) {
                continue;
            }

            final Date subEffDt = submittedCycleChange.getEffectiveDate();
            final Date nextEffDt = cycle.getEffectiveDate();

            // if submitted eff date is earlier than previous cycle's effective date.
            if (isBefore(subEffDt, nextEffDt)) {
                validEffDate.setIsValid(true);
                break;
            }

            // if submitted effective date is later than the previous cycle's
            // effective date
            if (isAfter(subEffDt, nextEffDt)) {
                if (validEffDate.getEffectiveDates().isEmpty()) {
                    validEffDate.setIsValid(false);
                }
                break;
            }

            // count the effective date
            if (isEqual(subEffDt, nextEffDt)) {
                validEffDate.addEffectiveDate(subEffDt);
            }

            int sameEffDtCount = validEffDate.getEffectiveDates().stream().filter(date -> {
                return isEqual(date, subEffDt);
            }).collect(Collectors.toList()).size();

            if (sameEffDtCount >= MAXIMUM_SAME_EFFECTIVE_DATE) {
                validEffDate.setIsValid(false);
                break;
            }
        }

        return validEffDate;

    }

    private static ValidEffectiveDates processEffectiveDateFromEarlierEffectiveDates(
            final List<CycleChangeRequest> descOrderCycleChange, final CycleChangeRequest submittedCycleChange) {

        ValidEffectiveDates validEffDate = new ValidEffectiveDates();

        for (CycleChangeRequest cycle : descOrderCycleChange) {
            final Date subEffDt = submittedCycleChange.getEffectiveDate();
            final Date prevEffDt = cycle.getEffectiveDate();

            // if submitted effective date is earlier than the previous cycle's
            // effective date
            if (isBefore(subEffDt, prevEffDt)) {
                if (validEffDate.getEffectiveDates().isEmpty()) {
                    validEffDate.setIsValid(false);
                }
                break;
            }

            // count the effective date
            if (isEqual(subEffDt, prevEffDt)) {
                validEffDate.addEffectiveDate(subEffDt);
            }

            int sameEffDtCount = validEffDate.getEffectiveDates().stream().filter(date -> {
                return isEqual(date, subEffDt);
            }).collect(Collectors.toList()).size();

            if (sameEffDtCount >= MAXIMUM_SAME_EFFECTIVE_DATE) {
                validEffDate.setIsValid(false);
                break;
            }
        }

        return validEffDate;
    }

    private static CycleChangeRequest createCycleChangeRequest(final CycleSchedule cycleSchedule,
            final LocalDate runDate, final DayEnum runDay, final DayEnum defEffectiveDate, final String offsiteInd,
            RunSequenceEnum sequence, User user) {

        CycleChangeRequest schedule = new CycleChangeRequest();

        schedule.setCorpId(CorpEnum.DEFAULT_CORP.getCorpId());
        schedule.setDivId(cycleSchedule.getDivId());
        schedule.setRunDate(java.sql.Date.valueOf(runDate));

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        schedule.setCreateTimestamp(ts);

        schedule.setEffectiveDate(getEffectiveDate(runDate, defEffectiveDate));
        schedule.setEffectiveDayName(getDayName(schedule.getEffectiveDate()));
        schedule.setEffectiveDateBase(schedule.getEffectiveDate());

        schedule.setRunDayName(runDay.getDayName());
        schedule.setRunNumber(sequence.getRunSequence());
        schedule.setCycleChangeRequestType(CycleChangeRequestTypeEnum.BASE.getRequestType());
        schedule.setOffsiteIndicator(offsiteInd);
        schedule.setChangeStatusName(ChangeStatusEnum.BASE.getChangeStatus());

        schedule.setCreateUserId(user.getUsername());

        schedule.setExpiryTimestamp(getExpiryTimestamp());

        return schedule;
    }

    public static CycleChangeRequest cloneCycleChangeRequest(final CycleChangeRequest toClone) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(toClone), CycleChangeRequest.class);
    }
}