package com.ryan.temporarycyclechange.controller.resource.assembler;

import com.ryan.temporarycyclechange.controller.CycleChangeRequestController;
import com.ryan.temporarycyclechange.domain.CycleChangeRequest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

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
public class CycleChangeRequestResourceAssembler
        implements ResourceAssembler<CycleChangeRequest, Resource<CycleChangeRequest>> {

    @Override
    public Resource<CycleChangeRequest> toResource(CycleChangeRequest cycleChangeRequest) {
        return new Resource<>(cycleChangeRequest,
                linkTo(methodOn(CycleChangeRequestController.class).getCycleChangeById(cycleChangeRequest.getId()))
                        .withSelfRel());
    }

}