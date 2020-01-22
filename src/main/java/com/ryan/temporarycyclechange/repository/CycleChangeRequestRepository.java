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
 * @author rsapl00
 */
@Repository
public interface CycleChangeRequestRepository extends JpaRepository<CycleChangeRequest, Long> {

        public List<CycleChangeRequest> findByRunDateBetweenOrderByRunDateAscRunNumberAsc(Date startDate, Date endDate);

        public List<CycleChangeRequest> findByDivIdAndRunDateBetweenOrderByRunDateAsc(String divisionId,
                        Date startRunDate, Date endRunDate);

        public List<CycleChangeRequest> findByIdIn(List<Long> ids);

        public List<CycleChangeRequest> findByIdInOrderByDivIdAscRunDateAsc(List<Long> ids);

        // This query can be improved using dynamic JPQL
        @Query("SELECT c FROM CycleChangeRequest c WHERE c.divId = :divisionId AND c.expiryTimestamp >= :expiryTs AND c.runDate BETWEEN :startDate AND :endDate AND c.changeStatusName != 'CANCELED' AND c.changeStatusName != 'REJECTED' ORDER BY c.runDate ASC, c.effectiveDate ASC, c.runNumber ASC")
        public List<CycleChangeRequest> findActiveByDivIdAndBetweenRunDatesAsc(String divisionId, Date startDate,
                        Date endDate, Timestamp expiryTs);

        @Query("SELECT c FROM CycleChangeRequest c WHERE c.expiryTimestamp >= :expiryTs AND c.runDate BETWEEN :startDate AND :endDate AND c.changeStatusName != 'CANCELED' AND c.changeStatusName != 'REJECTED' ORDER BY c.divId ASC, c.runDate ASC, c.effectiveDate ASC, c.runNumber ASC")
        public List<CycleChangeRequest> findActiveByBetweenRunDatesAsc(Date startDate, Date endDate,
                        Timestamp expiryTs);

        @Query("SELECT c FROM CycleChangeRequest c WHERE c.divId = :divisionId AND c.expiryTimestamp >= :expiryTs AND c.runDate BETWEEN :startDate AND :endDate AND c.changeStatusName != 'CANCELED' AND c.changeStatusName != 'REJECTED' ORDER BY c.runDate DESC, c.effectiveDate DESC, c.runNumber DESC")
        public List<CycleChangeRequest> findActiveByDivIdAndBetweenRunDatesDesc(String divisionId, Date startDate,
                        Date endDate, Timestamp expiryTs);

        @Query("SELECT c FROM CycleChangeRequest c WHERE c.divId = :divisionId AND c.runDate = :runDate AND c.expiryTimestamp >= :expiryTs AND c.changeStatusName != 'CANCELED' AND c.changeStatusName != 'REJECTED' ORDER BY c.runDate ASC, c.runNumber ASC")
        public List<CycleChangeRequest> findByDivIdAndRunDateAndNotExpired(String divisionId, Date runDate,
                        Timestamp expiryTs);

        @Query("SELECT c FROM CycleChangeRequest c WHERE c.divId = :divisionId AND c.runDate BETWEEN :startDate AND :endDate AND c.changeStatusName != 'CANCELED' AND c.changeStatusName != 'REJECTED' ORDER BY c.runDate ASC, c.effectiveDate ASC, c.runNumber ASC")
        public List<CycleChangeRequest> findHistoryDivIdAndBetweenRunDatesAsc(String divisionId, Date startDate,
                        Date endDate);

}