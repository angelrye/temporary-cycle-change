package com.ryan.temporarycyclechange.domain.enums;

/**
 * 
 * @author rsapl00
 */
public enum CycleChangeRequestTypeEnum {
    
    BASE(""),
    ADD("ADD"),
    ADD_OFFSITE("ADD + OFFSITE"),
    MODIFY("MODIFY"),
    MODIFY_OFFSITE("MODIFY + OFFSITE"),
    CANCEL("CANCEL"),
    OFFSITE("OFFSITE");

    private String requestType;

    private CycleChangeRequestTypeEnum(final String requestType) {
        this.requestType = requestType;
    }

    public String getRequestType() {
        return this.requestType;
    }

    public static CycleChangeRequestTypeEnum getChangeRequestTypeEnum(final String requestType) {

        for (CycleChangeRequestTypeEnum type : CycleChangeRequestTypeEnum.values()) {
            if (type.getRequestType().equals(requestType)) {
                return type;
            }
        }

        return CycleChangeRequestTypeEnum.BASE;
    }

    public boolean isEquals(String requestType) {
        return this.getRequestType().equals(requestType);
    }
}