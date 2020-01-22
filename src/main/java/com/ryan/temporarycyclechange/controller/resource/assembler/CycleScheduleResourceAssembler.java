package com.ryan.temporarycyclechange.controller.resource.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import com.ryan.temporarycyclechange.controller.CycleScheduleController;
import com.ryan.temporarycyclechange.domain.CycleChangeRequest;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

/**
 * This class assembles the return value (HATEOAS) of a REST 
 * service.
 * 
 * @author rsapl00
 */
@Component
public class CycleScheduleResourceAssembler
        implements ResourceAssembler<CycleChangeRequest, Resource<CycleChangeRequest>> {

    @Override
    public Resource<CycleChangeRequest> toResource(CycleChangeRequest baseSchedule) {
        return new Resource<>(baseSchedule,
                linkTo(methodOn(CycleScheduleController.class).getBaseCycleSchedules(baseSchedule.getDivId(),
                        baseSchedule.getRunDate().toString(), baseSchedule.getRunDate().toString())).withSelfRel());
    }

}