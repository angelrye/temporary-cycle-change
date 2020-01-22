package com.ryan.temporarycyclechange.validation;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.ryan.temporarycyclechange.controller.resource.CycleChangeRequestDTO;
import com.ryan.temporarycyclechange.domain.CycleChangeRequest;
import com.ryan.temporarycyclechange.domain.enums.ChangeStatusEnum;
import com.ryan.temporarycyclechange.domain.enums.CycleChangeRequestTypeEnum;
import com.ryan.temporarycyclechange.domain.enums.OffsiteIndicatorEnum;
import com.ryan.temporarycyclechange.domain.enums.RunSequenceEnum;
import com.ryan.temporarycyclechange.exception.CycleChangeRequestOffsiteException;
import com.ryan.temporarycyclechange.repository.CycleChangeRequestRepository;
import com.ryan.temporarycyclechange.util.DateUtil;

/**
 * 
 * @author rsapl00
 */
public class CycleChangeOffsiteValidator extends Validator {

    public CycleChangeOffsiteValidator(CycleChangeRequestRepository repository) {
        super(repository);
    }

    public CycleChangeOffsiteValidator(CycleChangeRequestRepository repository, Validator validator) {
        super(repository, validator);
    }

    @Override
    public Boolean isValid(CycleChangeRequestDTO dto) {

        Date runDate = Date.valueOf(LocalDate.parse(dto.getRunDate()));

        if (validator != null && !validator.isValid(dto)) {
            return false;
        }

        // retrieve cycle change request by run date and not expired.
        final List<CycleChangeRequest> cycleChangeRequests = repository
                .findByDivIdAndRunDateAndNotExpired(dto.getDivId(), runDate, DateUtil.getExpiryTimestamp());

        if (cycleChangeRequests.isEmpty()) {
            return true;
        }

        cycleChangeRequests.stream().forEach(cycle -> {

            if (ChangeStatusEnum.REJECTED.isEquals(cycle.getChangeStatusName())
                    || ChangeStatusEnum.CANCELLED.isEquals(cycle.getChangeStatusName())) {
                return;
            }

            if (ChangeStatusEnum.APPROVED.isEquals(cycle.getChangeStatusName())
                    && CycleChangeRequestTypeEnum.CANCEL.isEquals(cycle.getCycleChangeRequestType())) {
                return;
            }

            if (!cycle.getId().equals(dto.getId())) {

                if (cycle.getOffsiteIndicator().equals(OffsiteIndicatorEnum.NON_OFFSITE.getIndicator())) {
                    if (cycle.getRunNumber().equals(RunSequenceEnum.SECOND.getRunSequence())) {
                        if (dto.getOffsiteIndicator().equals(OffsiteIndicatorEnum.OFFSITE.getIndicator())) {
                            throw new CycleChangeRequestOffsiteException(
                                    "Invalid offsite schedule. You cannot request Offsite on Run1 only for a 2 run cycle. "
                                            + "Offsite can be Run2 ONLY or Run1 AND Run2 for a 2 run cycle.");
                        }
                    }
                } else if (cycle.getOffsiteIndicator().equals(OffsiteIndicatorEnum.OFFSITE.getIndicator())) {
                    if (cycle.getRunNumber().equals(RunSequenceEnum.FIRST.getRunSequence())) {
                        if (dto.getOffsiteIndicator().equals(OffsiteIndicatorEnum.NON_OFFSITE.getIndicator())) {
                            throw new CycleChangeRequestOffsiteException(
                                    "Invalid offsite schedule. You cannot request Offsite on Run1 only for a 2 run cycle. "
                                            + "Offsite can be Run2 ONLY or Run1 AND Run2 for a 2 run cycle.");
                        }
                    }
                }
            }
        });
        return true;
    }

}