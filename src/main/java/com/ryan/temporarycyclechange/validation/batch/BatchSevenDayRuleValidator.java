package com.ryan.temporarycyclechange.validation.batch;

import java.time.LocalDate;

import com.ryan.temporarycyclechange.controller.resource.CycleChangeRequestDTO;
import com.ryan.temporarycyclechange.exception.BufferDaysException;
import com.ryan.temporarycyclechange.repository.CycleChangeRequestRepository;
import com.ryan.temporarycyclechange.validation.Validator;

public class BatchSevenDayRuleValidator extends Validator {

    private Boolean skip7DayRule;

    public BatchSevenDayRuleValidator(CycleChangeRequestRepository repository) {
        super(repository);
    }

    public BatchSevenDayRuleValidator(CycleChangeRequestRepository repository, Boolean skip7DayRule) {
        super(repository);
        this.skip7DayRule = skip7DayRule;
    }
    
    public BatchSevenDayRuleValidator(Boolean skip7DayRule) {
        super();
        this.skip7DayRule = skip7DayRule;
    }

    @Override
    public Boolean isValid(CycleChangeRequestDTO dto) {
        LocalDate lRunDate = LocalDate.parse(dto.getRunDate());
        LocalDate currentDate = LocalDate.now();

        if (!skip7DayRule) {
            if (lRunDate.isBefore(currentDate.plusDays(7))) {
                throw new BufferDaysException();
            }
        }

        return true;
    }
    
}