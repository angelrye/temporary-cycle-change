package com.ryan.temporarycyclechange.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ryan.temporarycyclechange.domain.CycleChangeRequest;
import com.ryan.temporarycyclechange.repository.CycleScheduleRepository;
import com.ryan.temporarycyclechange.security.userdetails.RoleType;
import com.ryan.temporarycyclechange.security.userdetails.User;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author rsapl00
 */
@Service
@Transactional(readOnly = true)
public class CycleScheduleService {

    final private CycleChangeRequestService cycleChangeRequestService;
    final private CycleScheduleRepository cycleScheduleRepository;

    protected CycleScheduleService(CycleChangeRequestService cycleChangeRequestService,
            CycleScheduleRepository repository) {
        this.cycleChangeRequestService = cycleChangeRequestService;
        this.cycleScheduleRepository = repository;
    }

    public List<CycleChangeRequest> findBaseCycleSchedule(final String divisionId, final Date startDate,
            final Date endDate) {

        return cycleChangeRequestService.generateCycleChangeRequestBasedOnCycleSchedule(divisionId, startDate, endDate,
                new ArrayList<>(), true);

    }

    public List<String> findDistinctDivision() {

        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user.getRoles().contains(RoleType.USER_ADMIN)) {
            return cycleScheduleRepository.findAllDistinctDivId();
        } else {
            return Arrays.asList(cycleScheduleRepository.findDistinctDivIdByDivId(user.getDivision()));
        }

    }

}