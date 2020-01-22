package com.ryan.temporarycyclechange.validation;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author rsapl00
 */
public class ValidEffectiveDates {

    private Boolean valid = true;

    private List<Date> effectiveDates = new ArrayList<>();

    public void addEffectiveDate(Date effectiveDate) {
        effectiveDates.add(effectiveDate);
    }

    public Boolean IsValid() {
        return valid;
    }

    public void setIsValid(Boolean valid) {
        this.valid = valid;
    }

    public List<Date> getEffectiveDates() {
        return effectiveDates;
    }

    public void setEffectiveDates(List<Date> effectiveDates) {
        this.effectiveDates = effectiveDates;
    }

    
}