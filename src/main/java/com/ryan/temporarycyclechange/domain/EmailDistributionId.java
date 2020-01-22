package com.ryan.temporarycyclechange.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 
 * @author rsapl00
 */
@Data
@NoArgsConstructor
public class EmailDistributionId implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @NonNull
    private String corpId;

    @NonNull
    private String divId;

    @NonNull
    private String email;

    @NonNull
    private Timestamp createTimestamp;
}