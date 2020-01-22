package com.ryan.temporarycyclechange.controller.resource;

import java.sql.Date;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.ryan.temporarycyclechange.domain.CycleChangeRequest;
import com.ryan.temporarycyclechange.util.DateUtil;
import com.ryan.temporarycyclechange.validation.ChronologicalOrderDateConstraint;
import com.ryan.temporarycyclechange.validation.CycleChangeRequestConstraint;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Data Transfer Object that will handle inputs from the client side.
 * 
 * Used when adding new run or modifying existing cycle change request.
 * 
 * @author rsapl00
 */
@Data
@NoArgsConstructor
@ChronologicalOrderDateConstraint.List({
        @ChronologicalOrderDateConstraint(startDate = "runDate", endDate = "effectiveDate", message = "Effective date should be later than run date.") })
@CycleChangeRequestConstraint
public class CycleChangeRequestDTO {

    private Long id;

    @NonNull
    @NotNull(message = "Division ID is required.")
    @NotBlank(message = "Division ID is required.")
    private String divId;

    @NonNull
    private String runDate;

    @NonNull
    private String effectiveDate;

    @NonNull
    private String comment;

    @NonNull
    @NotBlank(message = "Offsite indicator is required. Accepted values are 0 and 1 only.")
    @NotNull(message = "Offsite indicator is required. Accepted values are 0 and 1 only.")
    private String offsiteIndicator;

    public CycleChangeRequest getCycleChangeRequest() {
        final CycleChangeRequest request = new CycleChangeRequest();
        request.setId(this.id);
        request.setDivId(this.divId);
        request.setRunDate(Date.valueOf(LocalDate.parse(this.runDate)));
        request.setEffectiveDate(Date.valueOf(LocalDate.parse(this.effectiveDate)));
        request.setOffsiteIndicator(this.offsiteIndicator);
        request.setComment(this.comment);
        
        return request;
    }

    public static CycleChangeRequestDTO build(final CycleChangeRequest request) {
        final CycleChangeRequestDTO dto = new CycleChangeRequestDTO();

        dto.setDivId(request.getDivId());
        dto.setRunDate(DateUtil.convertDateToString(request.getRunDate()));
        dto.setEffectiveDate(DateUtil.convertDateToString(request.getEffectiveDate()));
        dto.setComment(request.getComment());
        dto.setOffsiteIndicator(request.getOffsiteIndicator());
        
        if (request.getId() == null) {
            dto.setId(0l);
        } else {
            dto.setId(request.getId());
        }

        return dto;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Run Date: ")
            .append(this.getRunDate())
            .append(", Effective Date: ")
            .append(this.getEffectiveDate());

        return sb.toString();
    }
}