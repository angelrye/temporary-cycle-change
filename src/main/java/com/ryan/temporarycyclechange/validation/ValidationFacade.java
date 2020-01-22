package com.ryan.temporarycyclechange.validation;

import com.ryan.temporarycyclechange.controller.resource.CycleChangeRequestDTO;
import com.ryan.temporarycyclechange.repository.CycleChangeRequestRepository;

import org.springframework.stereotype.Component;

/**
 * 
 * @author rsapl00
 */
@Component
public class ValidationFacade {

    private CycleChangeRequestRepository repository;

    public ValidationFacade(CycleChangeRequestRepository repository) {
        this.repository = repository;
    }

    public Boolean isvalid(CycleChangeRequestDTO cycleChangeDto, Boolean skip7DayRule) {

        Validator sevenDayRule = new SevenDayRuleValidator(repository, skip7DayRule);
        Validator duplicateValidator = new CycleChangeDuplicateRunAndEffectiveValidator(repository, sevenDayRule);
        Validator sequenceValidator = new CycleChangeRunSequenceValidator(repository, duplicateValidator);
        Validator effDateValidator = new CycleChangeEffectiveDateValidator(repository, sequenceValidator);
        Validator runDateValidator = new CycleChangeRunDateValidator(repository, effDateValidator);

        return runDateValidator.isValid(cycleChangeDto);

    }

}