package com.ryan.temporarycyclechange.validation;

import static com.ryan.temporarycyclechange.util.DateUtil.isEqual;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.ryan.temporarycyclechange.controller.resource.CycleChangeRequestDTO;
import com.ryan.temporarycyclechange.domain.CycleChangeRequest;
import com.ryan.temporarycyclechange.domain.enums.ChangeStatusEnum;
import com.ryan.temporarycyclechange.domain.enums.CycleChangeRequestTypeEnum;
import com.ryan.temporarycyclechange.domain.enums.RunSequenceEnum;
import com.ryan.temporarycyclechange.exception.CycleChangeNotFoundException;
import com.ryan.temporarycyclechange.exception.MaximumRunSchedulePerRunDateException;
import com.ryan.temporarycyclechange.repository.CycleChangeRequestRepository;
import com.ryan.temporarycyclechange.util.DateUtil;

/**
 * 
 * @author rsapl00
 */
public class CycleChangeRunDateValidator extends Validator {

    public CycleChangeRunDateValidator(CycleChangeRequestRepository repository) {
        super(repository);
    }

    public CycleChangeRunDateValidator(CycleChangeRequestRepository repository, Validator validator) {
        super(repository, validator);
    }

    /**
     * This validates the run date of the requested cycle change.
     * 
     * Run date should be 7 days after the current date.
     * 
     * 7 Day Rule will be skipped for generation of cycle change calendar requests
     * but will be applied when adding a new run or modifying a cycle change.
     * 
     * For modification of Cycle Change, Run date will be included in the request
     * but should not / can not be modified. Only Effective Date should be allowed
     * to be modified.
     * 
     * For addition of new Cycle Change, Run Date is also included and will be
     * validated the specified run date reached maximum allowed (2) per week.
     */
    @Override
    public Boolean isValid(final CycleChangeRequestDTO dto) {

        if (validator != null && !validator.isValid(dto)) {
            return false;
        }

        LocalDate lRunDate = LocalDate.parse(dto.getRunDate());
        Date runDate = Date.valueOf(lRunDate);

        // This will assume that if Id is set to 0 in the request then the request is
        // for new cycle change
        final boolean isAddAction = dto.getId().equals(Long.valueOf(0l)) ? true : false;

        if (isAddAction) {
            final List<CycleChangeRequest> cycleChangeRequests = repository
                    .findByDivIdAndRunDateAndNotExpired(dto.getDivId(), runDate, DateUtil.getExpiryTimestamp());

            if (cycleChangeRequests.isEmpty()) {
                return true;
            }

            int count = cycleChangeRequests.stream().mapToInt(cycle -> {
                if (!cycle.getId().equals(dto.getId())) {

                    if (!(ChangeStatusEnum.REJECTED.isEquals(cycle.getChangeStatusName())
                            || ChangeStatusEnum.CANCELLED.isEquals(cycle.getChangeStatusName()))) {

                        if (CycleChangeRequestTypeEnum.CANCEL.isEquals(cycle.getCycleChangeRequestType())) {
                            return 0;
                        }

                        return 1;
                    } else {
                        return 0;
                    }

                }
                return 0;
            }).sum();

            if (count >= RunSequenceEnum.SECOND.getRunSequence()) {
                throw new MaximumRunSchedulePerRunDateException(
                        "Maximum schedule per run date reached. Only two (2) same run date is accepted. Run Date: " + dto.getRunDate());
            }

        } else {
            final Date dtoRunDate = Date.valueOf(LocalDate.parse(dto.getRunDate()));
            repository.findById(dto.getId()).map(cycle -> {
                if (!(isEqual(cycle.getRunDate(), dtoRunDate))) {
                    throw new RuntimeException("Run date is not modifiable.");
                }
                return cycle;
            }).orElseThrow(() -> new CycleChangeNotFoundException("Cycle Change not found."));
        }

        return true;
    }
}