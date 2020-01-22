package com.ryan.temporarycyclechange.domain;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NonNull;

/**
 * 
 * @author rsapl00
 */
@Data
public class CycleChangeRequestId implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5553500302787794742L;


    @NonNull
    private String corpId;

    @NonNull
    private String divId;

    @NonNull
    private Date runDate;

    @NonNull
    private Date effectiveDate;

    @NonNull
    private Timestamp createTimestamp;

    public CycleChangeRequestId() {}

    public CycleChangeRequestId(String corpId, String divId, Date runDate, Date effectiveDate,
            Timestamp createTimestamp) {
        this.corpId = corpId;
        this.divId = divId;
        this.runDate = runDate;
        this.effectiveDate = effectiveDate;
        this.createTimestamp = createTimestamp;
    }
   
    
}