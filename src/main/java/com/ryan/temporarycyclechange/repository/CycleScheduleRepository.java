package com.ryan.temporarycyclechange.repository;

import java.util.List;

import com.ryan.temporarycyclechange.domain.CycleSchedule;
import com.ryan.temporarycyclechange.domain.CycleScheduleId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author rsapl00
 */
@Repository
public interface CycleScheduleRepository extends JpaRepository<CycleSchedule, CycleScheduleId> {

    public List<CycleSchedule> findByDivIdOrderByDayNumAsc(String divId);

    @Query("SELECT DISTINCT divId FROM CycleSchedule")
    public List<String> findAllDistinctDivId();

    @Query("SELECT DISTINCT divId FROM CycleSchedule WHERE divId = :divId")
    public String findDistinctDivIdByDivId(String divId);
}