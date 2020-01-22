package com.ryan.temporarycyclechange.validation.batch;

import com.ryan.temporarycyclechange.controller.resource.CycleChangeRequestDTO;
import com.ryan.temporarycyclechange.exception.ValidationException;
import com.ryan.temporarycyclechange.repository.CycleChangeRequestRepository;
import com.ryan.temporarycyclechange.validation.Validator;

import org.springframework.stereotype.Component;

/**
 * 
 * @author rsapl00
 */
@Component
public class BatchValidationFacade {

    private CycleChangeRequestRepository repository;

    public BatchValidationFacade(CycleChangeRequestRepository repository) {
        this.repository = repository;
    }

    public Boolean isvalid(CycleChangeRequestDTO cycleChangeDto, Boolean skip7DayRule) {

        Validator sevenDayRule = new BatchSevenDayRuleValidator(repository, skip7DayRule);
        Validator duplicateValidator = new BatchCycleChangeDuplicateRunAndEffectiveValidator(repository, sevenDayRule);
        Validator sequenceValidator = new BatchCycleChangeRunSequenceValidator(repository, duplicateValidator);
        Validator effDateValidator = new BatchCycleChangeEffectiveDateValidator(repository, sequenceValidator);
        Validator runDateValidator = new BatchCycleChangeRunDateValidator(repository, effDateValidator);

        if (!runDateValidator.isValid(cycleChangeDto)) {
            throw new ValidationException(cycleChangeDto.toString());
        }

        return true;
    }

}