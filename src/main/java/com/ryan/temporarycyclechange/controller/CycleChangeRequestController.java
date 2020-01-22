package com.ryan.temporarycyclechange.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.ryan.temporarycyclechange.controller.resource.CycleChangeRequestDTO;
import com.ryan.temporarycyclechange.controller.resource.CycleChangeSearchDTO;
import com.ryan.temporarycyclechange.controller.resource.CycleChangeSearchDTOByDateRange;
import com.ryan.temporarycyclechange.controller.resource.assembler.CycleChangeRequestResourceAssembler;
import com.ryan.temporarycyclechange.domain.CycleChangeRequest;
import com.ryan.temporarycyclechange.service.CycleChangeRequestService;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handle all REST service for Cycle Changes Request and.
 * 
 * @author rsapl00
 */
@RestController
@RequestMapping("/rest")
public class CycleChangeRequestController {

        private CycleChangeRequestService cycleChangeRequestService;
        private CycleChangeRequestResourceAssembler assembler;

        public CycleChangeRequestController(CycleChangeRequestService cycleChangeRequestService,
                        CycleChangeRequestResourceAssembler assembler) {
                this.cycleChangeRequestService = cycleChangeRequestService;
                this.assembler = assembler;
        }

        @GetMapping("/cyclechanges")
        public Resources<Resource<CycleChangeRequest>> getAllCycleChangeRequests() {
                List<Resource<CycleChangeRequest>> cycleChanges = cycleChangeRequestService.findAll().stream()
                                .map(assembler::toResource).collect(Collectors.toList());

                return new Resources<>(cycleChanges,
                                linkTo(methodOn(CycleChangeRequestController.class).getAllCycleChangeRequests())
                                                .withSelfRel());
        }

        @PostMapping("/cyclechanges")
        public ResponseEntity<?> submitNewCycleChange(@Valid @RequestBody CycleChangeRequestDTO newCycleChange)
                        throws URISyntaxException {

                Resource<CycleChangeRequest> resource = assembler.toResource(cycleChangeRequestService
                                .saveNewCycleChangeRequest(newCycleChange.getCycleChangeRequest()));

                return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
        }

        @PostMapping("/cyclechanges/schedules")
        public ResponseEntity<?> getCycleChangeByRunDateFromAndTo(@Valid @RequestBody CycleChangeSearchDTO cycleChange)
                        throws URISyntaxException {

                List<Resource<CycleChangeRequest>> cycleChanges = cycleChangeRequestService
                                .generateCycleChangeRequest(cycleChange.getDivisionId(),
                                                cycleChange.getStartDateAsDate(), cycleChange.getEndDateAsDate())
                                .stream().map(assembler::toResource).collect(Collectors.toList());

                return ResponseEntity
                                .created(new URI(linkTo(methodOn(CycleChangeRequestController.class)
                                                .getCycleChangeByRunDateFromAndTo(cycleChange)).withSelfRel()
                                                                .getHref()))
                                .body(new Resources<>(cycleChanges,
                                                linkTo(methodOn(CycleChangeRequestController.class)
                                                                .getCycleChangeByRunDateFromAndTo(cycleChange))
                                                                                .withSelfRel()));
        }

        @GetMapping("/cyclechanges/{id}")
        public Resource<CycleChangeRequest> getCycleChangeById(@PathVariable Long id) {

                CycleChangeRequest cycleChange = cycleChangeRequestService.findById(id);

                return new Resource<>(cycleChange,
                                linkTo(methodOn(CycleChangeRequestController.class).getCycleChangeById(id))
                                                .withSelfRel());
        }

        @PutMapping("/cyclechanges/update")
        public ResponseEntity<?> updateCycleChangeRequest(@Valid @RequestBody CycleChangeRequestDTO cycleChangeRequest)
                        throws URISyntaxException {

                Resource<CycleChangeRequest> resource = assembler.toResource(cycleChangeRequestService
                                .updateCycleChangeRequest(cycleChangeRequest.getCycleChangeRequest()));

                return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
        }

        @PutMapping("/cyclechanges/approve")
        public ResponseEntity<?> approveMultipleCycleChangeRequest(@RequestBody List<Long> ids)
                        throws URISyntaxException {

                List<Resource<CycleChangeRequest>> approvedRequests = cycleChangeRequestService
                                .approveMultipleCycleChangeRequest(ids).stream().map(assembler::toResource)
                                .collect(Collectors.toList());

                return ResponseEntity
                                .created(new URI(linkTo(methodOn(CycleChangeRequestController.class)
                                                .approveMultipleCycleChangeRequest(ids)).withSelfRel().getHref()))
                                .body(new Resources<>(approvedRequests,
                                                linkTo(methodOn(CycleChangeRequestController.class)
                                                                .approveMultipleCycleChangeRequest(ids))
                                                                                .withSelfRel()));
        }

        @PutMapping("/cyclechanges/reject")
        public ResponseEntity<?> rejectMultipleCycleChangeRequest(@RequestBody List<Long> ids)
                        throws URISyntaxException {

                List<Resource<CycleChangeRequest>> approvedRequests = cycleChangeRequestService
                                .rejectMultipleCycleChangeRequest(ids).stream().map(assembler::toResource)
                                .collect(Collectors.toList());

                return ResponseEntity
                                .created(new URI(linkTo(methodOn(CycleChangeRequestController.class)
                                                .rejectMultipleCycleChangeRequest(ids)).withSelfRel().getHref()))
                                .body(new Resources<>(approvedRequests,
                                                linkTo(methodOn(CycleChangeRequestController.class)
                                                                .rejectMultipleCycleChangeRequest(ids)).withSelfRel()));
        }

        @PutMapping("/cyclechanges/forapproval")
        public ResponseEntity<?> forApprovalCycleChangeRequest(@RequestBody List<Long> ids) throws URISyntaxException {
                List<Resource<CycleChangeRequest>> approvedRequests = cycleChangeRequestService
                                .forApprovalCycleChangeRequest(ids).stream().map(assembler::toResource)
                                .collect(Collectors.toList());

                return ResponseEntity.created(new URI(
                                linkTo(methodOn(CycleChangeRequestController.class).forApprovalCycleChangeRequest(ids))
                                                .withSelfRel().getHref()))
                                .body(new Resources<>(approvedRequests,
                                                linkTo(methodOn(CycleChangeRequestController.class)
                                                                .forApprovalCycleChangeRequest(ids)).withSelfRel()));
        }

        @PostMapping("/cyclechanges/forapproval/sent-to-email")
        public ResponseEntity<?> sendForApprovalRequestsToEmail(
                        @Valid @RequestBody CycleChangeSearchDTOByDateRange cycleChange) {

                cycleChangeRequestService.sendForApprovalRequestViaEmail(cycleChange.getStartDateAsDate(),
                                cycleChange.getEndDateAsDate());

                return ResponseEntity.noContent().build();
        }

        @PutMapping("/cyclechanges/cancel/request")
        public ResponseEntity<?> cancelCycleChangeRequest(@RequestBody List<Long> ids) throws URISyntaxException {
                List<Resource<CycleChangeRequest>> approvedRequests = cycleChangeRequestService
                                .cancelCycleChangeRequest(ids).stream().map(assembler::toResource)
                                .collect(Collectors.toList());

                return ResponseEntity.created(new URI(
                                linkTo(methodOn(CycleChangeRequestController.class).cancelCycleChangeRequest(ids))
                                                .withSelfRel().getHref()))
                                .body(new Resources<>(approvedRequests,
                                                linkTo(methodOn(CycleChangeRequestController.class)
                                                                .cancelCycleChangeRequest(ids)).withSelfRel()));
        }

        @PutMapping("/cyclechanges/cancel/run")
        public ResponseEntity<?> cancelCycleChangeRun(@RequestBody List<Long> ids) throws URISyntaxException {
                List<Resource<CycleChangeRequest>> approvedRequests = cycleChangeRequestService
                                .cancelCycleChangeRun(ids).stream().map(assembler::toResource)
                                .collect(Collectors.toList());

                return ResponseEntity.created(
                                new URI(linkTo(methodOn(CycleChangeRequestController.class).cancelCycleChangeRun(ids))
                                                .withSelfRel().getHref()))
                                .body(new Resources<>(approvedRequests,
                                                linkTo(methodOn(CycleChangeRequestController.class)
                                                                .cancelCycleChangeRequest(ids)).withSelfRel()));
        }

        @PostMapping("/cyclechanges/forapproval/validate")
        public ResponseEntity<?> validateEntries(@RequestBody List<Long> ids) throws URISyntaxException {

                List<Resource<CycleChangeRequest>> valids = cycleChangeRequestService.validateEntries(ids).stream()
                                .map(assembler::toResource).collect(Collectors.toList());

                return ResponseEntity.created(
                                new URI(linkTo(methodOn(CycleChangeRequestController.class).validateEntries(ids))
                                                .withSelfRel().getHref()))
                                .body(new Resources<>(valids, linkTo(
                                                methodOn(CycleChangeRequestController.class).validateEntries(ids))
                                                                .withSelfRel()));
        }
}