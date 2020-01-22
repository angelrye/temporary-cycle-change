package com.ryan.temporarycyclechange.validation.batch;

import static com.ryan.temporarycyclechange.util.DateUtil.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.ryan.temporarycyclechange.controller.resource.CycleChangeRequestDTO;
import com.ryan.temporarycyclechange.domain.CycleChangeRequest;
import com.ryan.temporarycyclechange.domain.enums.BufferDayEnum;
import com.ryan.temporarycyclechange.domain.enums.ChangeStatusEnum;
import com.ryan.temporarycyclechange.domain.enums.CycleChangeRequestTypeEnum;
import com.ryan.temporarycyclechange.domain.enums.RunSequenceEnum;
import com.ryan.temporarycyclechange.exception.BufferDaysException;
import com.ryan.temporarycyclechange.exception.ChronologicalDateException;
import com.ryan.temporarycyclechange.exception.InvalidEffectiveDate;
import com.ryan.temporarycyclechange.exception.RunSequenceException;
import com.ryan.temporarycyclechange.repository.CycleChangeRequestRepository;
import com.ryan.temporarycyclechange.util.DateUtil;
import com.ryan.temporarycyclechange.validation.ValidEffectiveDates;
import com.ryan.temporarycyclechange.validation.Validator;

/**
 * 
 * @author rsapl00
 */
public class BatchCycleChangeEffectiveDateValidator extends Validator {

    public BatchCycleChangeEffectiveDateValidator(CycleChangeRequestRepository repository) {
        super(repository);
    }

    public BatchCycleChangeEffectiveDateValidator(CycleChangeRequestRepository repository, Validator validator) {
        super(repository, validator);
    }

    @Override
    public Boolean isValid(final CycleChangeRequestDTO dto) {

        if (validator != null && !validator.isValid(dto)) {
            return false;
        }

        LocalDate lRunDate = LocalDate.parse(dto.getRunDate());
        LocalDate lEffDate = LocalDate.parse(dto.getEffectiveDate());

        if (DateUtil.isAfter(lEffDate, lRunDate.plusDays(7))) {
            throw new BufferDaysException("Effective date should be within seven (7) days from the run date.");
        }

        return validateEffectiveDate(dto);
    }

    private boolean validateEffectiveDate(final CycleChangeRequestDTO dto) {

        Date runDate = Date.valueOf(LocalDate.parse(dto.getRunDate()));

        List<CycleChangeRequest> prevWeek = repository.findActiveByDivIdAndBetweenRunDatesDesc(dto.getDivId(),
                DateUtil.getBufferDate(runDate, BufferDayEnum.MINUS_BUFFER), runDate, DateUtil.getExpiryTimestamp());

        List<CycleChangeRequest> nextWeek = repository.findActiveByDivIdAndBetweenRunDatesAsc(dto.getDivId(), runDate,
                DateUtil.getBufferDate(runDate, BufferDayEnum.PLUS_BUFFER), DateUtil.getExpiryTimestamp());

        return validateCycleChangeEffectiveDate(prevWeek, nextWeek, dto);
    }

    private Boolean validateCycleChangeEffectiveDate(final List<CycleChangeRequest> beforeCycleChanges,
            final List<CycleChangeRequest> afterCycleChanges, final CycleChangeRequestDTO dto) {

        Date effectiveDate = Date.valueOf(LocalDate.parse(dto.getEffectiveDate()));

        ValidEffectiveDates before = processEffectiveDateFromEarlierEffectiveDates(beforeCycleChanges, dto);
        ValidEffectiveDates after = processEffectiveDateFromLaterEffectiveDates(afterCycleChanges, dto);

        boolean isBothValid = before.IsValid() && after.IsValid();

        int sameEffDtCount = before.getEffectiveDates().stream().filter(date -> {
            return DateUtil.isEqual(date, effectiveDate);
        }).collect(Collectors.toList()).size() + after.getEffectiveDates().stream().filter(date -> {
            return DateUtil.isEqual(date, effectiveDate);
        }).collect(Collectors.toList()).size();

        if (sameEffDtCount >= RunSequenceEnum.SECOND.getRunSequence()) {
            throw new InvalidEffectiveDate(
                    "Invalid effective date. Only two consecutive same effective date is valid.");
        }

        if (sameEffDtCount <= 1 && isBothValid) {
            return true;
        }

        return false;
    }

    private ValidEffectiveDates processEffectiveDateFromLaterEffectiveDates(
            final List<CycleChangeRequest> ascOrderCycleChange, final CycleChangeRequestDTO dto) {

        ValidEffectiveDates validEffDate = new ValidEffectiveDates();

        Date runDate = Date.valueOf(LocalDate.parse(dto.getRunDate()));
        Date effectiveDate = Date.valueOf(LocalDate.parse(dto.getEffectiveDate()));

        for (CycleChangeRequest cycle : ascOrderCycleChange) {

            // skip own record
            if (cycle.getId().equals(dto.getId())) {
                continue;
            }

            if (ChangeStatusEnum.REJECTED.isEquals(cycle.getChangeStatusName())
                    || ChangeStatusEnum.CANCELLED.isEquals(cycle.getChangeStatusName())) {
                continue;
            }

            if (CycleChangeRequestTypeEnum.CANCEL.isEquals(cycle.getCycleChangeRequestType())) {
                continue;
            }

            // skip same run date since same run date has been processed in
            // processEffectiveDateFromEarlierEffectiveDates
            if (DateUtil.isEqual(cycle.getRunDate(), runDate)) {
                continue;
            }

            final Date subEffDt = effectiveDate;
            final Date nextEffDt = cycle.getEffectiveDate();

            // if submitted eff date is earlier than previous cycle's effective date.
            if (DateUtil.isBefore(subEffDt, nextEffDt)) {
                validEffDate.setIsValid(true);
                break;
            }

            // if submitted effective date is later than the previous cycle's
            // effective date
            if (isAfter(subEffDt, nextEffDt)) {
                throw new ChronologicalDateException(dto.toString()); 
            }

            // count the effective date
            if (isEqual(subEffDt, nextEffDt)) {
                validEffDate.addEffectiveDate(subEffDt);
            }

            int sameEffDtCount = validEffDate.getEffectiveDates().stream().filter(date -> {
                return isEqual(date, subEffDt);
            }).collect(Collectors.toList()).size();

            if (sameEffDtCount >= RunSequenceEnum.SECOND.getRunSequence()) {
                throw new InvalidEffectiveDate("Invalid Effective Date. " + dto.toString());
            }
        }

        return validEffDate;

    }

    private ValidEffectiveDates processEffectiveDateFromEarlierEffectiveDates(
            final List<CycleChangeRequest> descOrderCycleChange, final CycleChangeRequestDTO dto) {

        ValidEffectiveDates validEffDate = new ValidEffectiveDates();

        Date runDate = Date.valueOf(LocalDate.parse(dto.getRunDate()));
        Date effectiveDate = Date.valueOf(LocalDate.parse(dto.getEffectiveDate()));

        for (CycleChangeRequest cycle : descOrderCycleChange) {

            // skip own record for validation
            if (cycle.getId().equals(dto.getId())) {
                continue;
            }

            if (cycle.getChangeStatusName().equals(ChangeStatusEnum.REJECTED.getChangeStatus())
                    || cycle.getChangeStatusName().equals(ChangeStatusEnum.CANCELLED.getChangeStatus())) {
                continue;
            }

            if (CycleChangeRequestTypeEnum.CANCEL.isEquals(cycle.getCycleChangeRequestType())) {
                continue;
            }

            final Date subEffDt = effectiveDate;
            final Date subRunDt = runDate;
            final Date prevEffDt = cycle.getEffectiveDate();
            final Date prevRunDt = cycle.getRunDate();

            if (isEqual(subRunDt, prevRunDt) && isBefore(subEffDt, prevEffDt)) {
                if (cycle.getRunNumber().equals(RunSequenceEnum.FIRST.getRunSequence())) {
                    if (!CycleChangeRequestTypeEnum.CANCEL.isEquals(cycle.getCycleChangeRequestType())) {
                        throw new RunSequenceException(dto.toString());
                    }
                }
            } else if (isBefore(subEffDt, prevEffDt)) {
                throw new ChronologicalDateException(dto.toString()); 
            }

            // count the effective date
            if (isEqual(subEffDt, prevEffDt)) {
                validEffDate.addEffectiveDate(subEffDt);
            }

            int sameEffDtCount = validEffDate.getEffectiveDates().stream().filter(date -> {
                return isEqual(date, subEffDt);
            }).collect(Collectors.toList()).size();

            if (sameEffDtCount >= RunSequenceEnum.SECOND.getRunSequence()) {
                throw new InvalidEffectiveDate("Invalid Effective Date. " + dto.toString());
            }
        }

        return validEffDate;
    }

}