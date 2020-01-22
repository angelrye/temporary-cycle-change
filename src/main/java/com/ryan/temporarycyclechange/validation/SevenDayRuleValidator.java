package com.ryan.temporarycyclechange.validation;

import java.time.LocalDate;

import com.ryan.temporarycyclechange.controller.resource.CycleChangeRequestDTO;
import com.ryan.temporarycyclechange.exception.BufferDaysException;
import com.ryan.temporarycyclechange.repository.CycleChangeRequestRepository;

public class SevenDayRuleValidator extends Validator {

    private Boolean skip7DayRule;

    public SevenDayRuleValidator(CycleChangeRequestRepository repository) {
        super(repository);
    }

    public SevenDayRuleValidator(CycleChangeRequestRepository repository, Boolean skip7DayRule) {
        super(repository);
        this.skip7DayRule = skip7DayRule;
    }
    
    public SevenDayRuleValidator(Boolean skip7DayRule) {
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