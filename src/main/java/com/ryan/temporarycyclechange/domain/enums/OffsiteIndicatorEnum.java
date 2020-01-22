package com.ryan.temporarycyclechange.domain.enums;

/**
 * 
 * @author rsapl00
 */
public enum OffsiteIndicatorEnum {
    NON_OFFSITE("0"),
    OFFSITE("1");

    private String indicator;

    private OffsiteIndicatorEnum(String indicator){
        this.indicator = indicator;
    }

    public String getIndicator() {
        return this.indicator;
    }

    public static OffsiteIndicatorEnum getOffsiteIndicator(String indicator) {
        for (OffsiteIndicatorEnum ind : OffsiteIndicatorEnum.values()) {
            if (ind.getIndicator().equals(indicator)) {
                return ind;
            }
        }

        return OffsiteIndicatorEnum.NON_OFFSITE;
    }
}