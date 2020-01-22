package com.ryan.temporarycyclechange.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 
 * @author rsapl00
 */
@Data
@NoArgsConstructor
@Entity
@Table (name = "PSDIVDST")
@IdClass(EmailDistributionId.class)
@EntityListeners(AuditingEntityListener.class)
public class EmailDistribution {

    @NonNull
    @Id
    @Column(name="CORP_ID")
    private String corpId;

    @NonNull
    @Id
    @Column(name="DIV_ID")
    private String divId;

    @NonNull
    @Id
    @Column(name="EMAIL_ADDR_TXT")
    private String email;

    @NonNull
    @Id
    @Column(name="CRT_TS")
    @CreatedDate
    private Timestamp createTimestamp;

    @NonNull
    @Column(name="CRT_USR_ID")
    @CreatedBy
    private String createUserId;
    
    @NonNull
    @Column(name="LST_UPD_TS")
    @LastModifiedDate
    private Timestamp lastUpdateTs;

    @NonNull
    @Column(name="LST_UPD_USR_ID")
    @LastModifiedBy
    private String lastUpdateUserId;
}