package com.ryan.temporarycyclechange.controller;

import static com.ryan.temporarycyclechange.util.DateUtil.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;

import com.ryan.temporarycyclechange.controller.resource.CycleChangeSearchDTO;
import com.ryan.temporarycyclechange.controller.resource.assembler.CycleChangeRequestResourceAssembler;
import com.ryan.temporarycyclechange.controller.resource.assembler.CycleScheduleResourceAssembler;
import com.ryan.temporarycyclechange.domain.CycleChangeRequest;
import com.ryan.temporarycyclechange.exception.ChronologicalDateException;
import com.ryan.temporarycyclechange.service.CycleChangeRequestService;
import com.ryan.temporarycyclechange.service.CycleScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles all REST service for Base Schedules, Divisions
 * and History.
 * 
 * @author rsapl00
 */
@RestController
@RequestMapping("/rest")
public class CycleScheduleController {

        private CycleScheduleService cycleScheduleService;
        private CycleScheduleResourceAssembler resourceAssembler;

        @Autowired
        private Validator validator;

        private CycleChangeRequestService cycleChangeRequestService;
        private CycleChangeRequestResourceAssembler requestAssembler;

        public CycleScheduleController(CycleScheduleService cycleScheduleService,
                        CycleScheduleResourceAssembler resourceAssembler,
                        CycleChangeRequestService cycleChangeRequestService,
                        CycleChangeRequestResourceAssembler requestAssembler) {
                this.cycleScheduleService = cycleScheduleService;
                this.resourceAssembler = resourceAssembler;
                this.cycleChangeRequestService = cycleChangeRequestService;
                this.requestAssembler = requestAssembler;
        }

        @GetMapping("/cycleschedules/{divisionId}")
        public Resources<Resource<CycleChangeRequest>> getBaseCycleSchedules(@PathVariable String divisionId,
                        @RequestParam String startDate, @RequestParam String endDate) {

                CycleChangeSearchDTO dtoToValidate = new CycleChangeSearchDTO();
                dtoToValidate.setDivisionId(divisionId);
                dtoToValidate.setStartDate(startDate);
                dtoToValidate.setEndDate(endDate);

                Set<ConstraintViolation<CycleChangeSearchDTO>> violations = validator.validate(dtoToValidate);
                if (violations.size() > 0) {
                        StringBuilder buffer = new StringBuilder();

                        violations.stream().forEach(v -> {
                                buffer.append(v.getMessage());
                        });

                        throw new ChronologicalDateException(buffer.toString());
                }

                List<Resource<CycleChangeRequest>> cycleSchedules = cycleScheduleService
                                .findBaseCycleSchedule(divisionId, convertStringToDate(startDate),
                                                convertStringToDate(endDate))
                                .stream().map(resourceAssembler::toResource).collect(Collectors.toList());

                return new Resources<>(cycleSchedules, linkTo(methodOn(CycleScheduleController.class)
                                .getBaseCycleSchedules(divisionId, startDate, endDate)).withSelfRel());
        }

        @GetMapping("/divisions")
        public Resources<Resource<String>> getDivisions() {
                List<Resource<String>> divisions = cycleScheduleService.findDistinctDivision().stream()
                                .map(division -> new Resource<>(division,
                                                linkTo(methodOn(CycleScheduleController.class).getDivisions())
                                                                .withSelfRel()))
                                .collect(Collectors.toList());

                return new Resources<>(divisions,
                                linkTo(methodOn(CycleScheduleController.class).getDivisions()).withSelfRel());
        }

        @PostMapping("/history")
        public ResponseEntity<?> getHistoryRecord(@Valid @RequestBody CycleChangeSearchDTO cycleChange)
                        throws URISyntaxException {

                List<Resource<CycleChangeRequest>> cycleChanges = cycleChangeRequestService
                                .getHistoryRecord(cycleChange.getDivisionId(), cycleChange.getStartDateAsDate(),
                                                cycleChange.getEndDateAsDate())
                                .stream().map(requestAssembler::toResource).collect(Collectors.toList());

                return ResponseEntity.created(
                                new URI(linkTo(methodOn(CycleScheduleController.class).getHistoryRecord(cycleChange))
                                                .withSelfRel().getHref()))
                                .body(new Resources<>(cycleChanges, linkTo(
                                                methodOn(CycleScheduleController.class).getHistoryRecord(cycleChange))
                                                                .withSelfRel()));
        }

}