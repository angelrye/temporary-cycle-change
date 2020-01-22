package com.ryan.temporarycyclechange.domain.enums;

/**
 * 
 * @author rsapl00
 */
public enum CorpEnum {

    DEFAULT_CORP("001");

    private String corpId;

    private CorpEnum (final String corpId) {
        this.corpId = corpId;
    }

    public String getCorpId() {
        return this.corpId;
    }
}