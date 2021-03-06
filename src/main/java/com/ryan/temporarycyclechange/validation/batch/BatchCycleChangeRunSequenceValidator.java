package com.ryan.temporarycyclechange.validation.batch;

import static com.ryan.temporarycyclechange.util.DateUtil.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.ryan.temporarycyclechange.controller.resource.CycleChangeRequestDTO;
import com.ryan.temporarycyclechange.domain.CycleChangeRequest;
import com.ryan.temporarycyclechange.domain.enums.ChangeStatusEnum;
import com.ryan.temporarycyclechange.domain.enums.CycleChangeRequestTypeEnum;
import com.ryan.temporarycyclechange.domain.enums.RunSequenceEnum;
import com.ryan.temporarycyclechange.exception.InvalidEffectiveDate;
import com.ryan.temporarycyclechange.repository.CycleChangeRequestRepository;
import com.ryan.temporarycyclechange.util.DateUtil;
import com.ryan.temporarycyclechange.validation.Validator;

/**
 * 
 * @author rsapl00
 */
public class BatchCycleChangeRunSequenceValidator extends Validator {

    public BatchCycleChangeRunSequenceValidator(CycleChangeRequestRepository repository) {
        super(repository);
    }

    public BatchCycleChangeRunSequenceValidator(CycleChangeRequestRepository repository, Validator validator) {
        super(repository, validator);
    }

    @Override
    public Boolean isValid(CycleChangeRequestDTO dto) {

        if (validator != null && !validator.isValid(dto)) {
            return false;
        }

        final Date runDate = Date.valueOf(LocalDate.parse(dto.getRunDate()));
        final Date effectiveDate = Date.valueOf(LocalDate.parse(dto.getEffectiveDate()));

        final List<CycleChangeRequest> existingCycles = repository.findByDivIdAndRunDateAndNotExpired(dto.getDivId(),
                runDate, DateUtil.getExpiryTimestamp());

        repository.findById(dto.getId()).map(toBeUpdatedCycle -> {

            if (ChangeStatusEnum.REJECTED.isEquals(toBeUpdatedCycle.getChangeStatusName())
                    || ChangeStatusEnum.CANCELLED.isEquals(toBeUpdatedCycle.getChangeStatusName())) {
                return toBeUpdatedCycle;
            }

            if (CycleChangeRequestTypeEnum.CANCEL.isEquals(toBeUpdatedCycle.getCycleChangeRequestType())) {
                return toBeUpdatedCycle;
            }

            if (isEqual(toBeUpdatedCycle.getRunDate(), runDate)) {
                existingCycles.stream().forEach(cycle -> {
                    if (!cycle.getId().equals(toBeUpdatedCycle.getId())
                            && !CycleChangeRequestTypeEnum.CANCEL.isEquals(cycle.getCycleChangeRequestType())) {
                        if (cycle.getRunNumber().equals(RunSequenceEnum.SECOND.getRunSequence())) {
                            if (isAfter(effectiveDate, cycle.getEffectiveDate())) {
                                throw new InvalidEffectiveDate(
                                        "Effective Date of Run 2 is later than the request's effective date.");
                            }
                        }
                    }
                });
            }
            return toBeUpdatedCycle;
        });

        return true;
    }

}