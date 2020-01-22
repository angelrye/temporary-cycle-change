package com.ryan.temporarycyclechange.validation.batch;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.ryan.temporarycyclechange.controller.resource.CycleChangeRequestDTO;
import com.ryan.temporarycyclechange.domain.CycleChangeRequest;
import com.ryan.temporarycyclechange.domain.enums.ChangeStatusEnum;
import com.ryan.temporarycyclechange.domain.enums.CycleChangeRequestTypeEnum;
import com.ryan.temporarycyclechange.exception.DuplicateCycleChangeSchedule;
import com.ryan.temporarycyclechange.repository.CycleChangeRequestRepository;
import com.ryan.temporarycyclechange.util.CycleScheduleUtility;
import com.ryan.temporarycyclechange.util.DateUtil;
import com.ryan.temporarycyclechange.validation.Validator;

/**
 * 
 * @author rsapl00
 */
public class BatchCycleChangeDuplicateRunAndEffectiveValidator extends Validator {

    public BatchCycleChangeDuplicateRunAndEffectiveValidator(CycleChangeRequestRepository repository) {
        super(repository);
    }

    public BatchCycleChangeDuplicateRunAndEffectiveValidator(CycleChangeRequestRepository repository, Validator validator) {
        super(repository, validator);
    }

    @Override
    public Boolean isValid(CycleChangeRequestDTO dto) {

        if (validator != null && !validator.isValid(dto)) {
            return false;
        }

        LocalDate lRunDate = LocalDate.parse(dto.getRunDate());
        LocalDate lEffDate = LocalDate.parse(dto.getEffectiveDate());
        Date runDate = Date.valueOf(lRunDate);
        Date effDate = Date.valueOf(lEffDate);

        final List<CycleChangeRequest> cycleChangeRequests = repository
                .findByDivIdAndRunDateAndNotExpired(dto.getDivId(), runDate, DateUtil.getExpiryTimestamp());

        if (cycleChangeRequests.isEmpty()) {
            return true;
        }

        final List<CycleChangeRequest> filtered = CycleScheduleUtility.removeCancelRequestFromRequests(cycleChangeRequests);

        for (CycleChangeRequest cycle : filtered) {
            if (!cycle.getId().equals(dto.getId())) {

                if (DateUtil.isEqual(cycle.getRunDate(), runDate)
                        && DateUtil.isEqual(cycle.getEffectiveDate(), effDate)) {

                    if (!(ChangeStatusEnum.REJECTED.isEquals(cycle.getChangeStatusName())
                            || ChangeStatusEnum.CANCELLED.isEquals(cycle.getChangeStatusName()))) {

                        if (!CycleChangeRequestTypeEnum.CANCEL.isEquals(cycle.getCycleChangeRequestType())) {
                            throw new DuplicateCycleChangeSchedule(cycle.toString());
                        }
                    }

                }
            }
        }

        return true;
    }
}