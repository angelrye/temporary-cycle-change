package com.ryan.temporarycyclechange.controller.resource;

import java.sql.Date;
import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
public class CycleChangeSearchDTO {

    @NonNull
    @NotNull(message = "Division ID is required.")
    @NotEmpty(message = "Division ID is required.")
    private String divisionId;

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