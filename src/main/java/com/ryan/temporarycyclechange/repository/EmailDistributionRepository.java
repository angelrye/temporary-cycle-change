package com.ryan.temporarycyclechange.repository;

import java.util.List;

import com.ryan.temporarycyclechange.domain.EmailDistribution;
import com.ryan.temporarycyclechange.domain.EmailDistributionId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author rsapl00
 */
@Repository
public interface EmailDistributionRepository extends JpaRepository<EmailDistribution, EmailDistributionId> {

    public List<EmailDistribution> findByDivId(String divId);

    @Query("SELECT e FROM EmailDistribution e WHERE e.divId = :adminDivId")
    public List<EmailDistribution> getAdminEmailDistribution(String adminDivId);

    @Query("SELECT e FROM EmailDistribution e WHERE e.divId IN (:divIds)")
    public List<EmailDistribution> findByDivIds(List<String> divIds);
    
}