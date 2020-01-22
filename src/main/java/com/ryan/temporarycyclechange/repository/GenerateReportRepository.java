package com.ryan.temporarycyclechange.repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.ryan.temporarycyclechange.domain.CycleChangeRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author jchin13
 */
@Repository
public interface GenerateReportRepository extends JpaRepository<CycleChangeRequest, Long> {

    // RIM
    @Query("SELECT c FROM CycleChangeRequest c WHERE c.divId = :divisionId AND c.runDate BETWEEN :startDate AND :endDate AND c.changeStatusName != 'CANCELED' AND c.changeStatusName != 'REJECTED' AND c.expiryTimestamp >= :expiryDate  ORDER BY c.divId, c.runDate ASC, c.effectiveDate ASC")
    public List<CycleChangeRequest> findActiveByDivIdAndBetweenRunDatesAsc(String divisionId, Date startDate,
            Date endDate, Timestamp expiryDate);

    // Admin
    @Query("SELECT c FROM CycleChangeRequest c WHERE c.runDate BETWEEN :startDate AND :endDate AND c.changeStatusName != 'CANCELED' AND c.changeStatusName != 'REJECTED' AND c.expiryTimestamp >= :expiryDate ORDER BY c.divId, c.runDate ASC, c.effectiveDate ASC")
    public List<CycleChangeRequest> findActiveByBetweenRunDatesAsc(Date startDate, Date endDate, Timestamp expiryDate);

}