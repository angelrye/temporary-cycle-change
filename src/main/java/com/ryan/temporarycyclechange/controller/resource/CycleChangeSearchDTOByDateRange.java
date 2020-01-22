package com.ryan.temporarycyclechange.controller.resource;

import java.sql.Date;
import java.time.LocalDate;

import com.ryan.temporarycyclechange.validation.ChronologicalOrderDateConstraint;

import io.micrometer.core.lang.NonNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object that will handle inputs from the client side.
 * 
 * Used when when searching history or generating cycle change calendar.
 * 
 * @author rsapl00
 */
@Data
@NoArgsConstructor
@ChronologicalOrderDateConstraint.List({
        @ChronologicalOrderDateConstraint(startDate = "startDate", endDate = "endDate", message = "End date should be later than start date.") })
public class CycleChangeSearchDTOByDateRange {

    @NonNull
    private String startDate;

    @NonNull
    private String endDate;

    public Date getStartDateAsDate() {
        return Date.valueOf(LocalDate.parse(startDate));
    }

    public Date getEndDateAsDate() {
        return Date.valueOf(LocalDate.parse(endDate));
    }

}