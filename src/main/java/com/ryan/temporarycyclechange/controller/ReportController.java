package com.ryan.temporarycyclechange.controller;

import java.net.URISyntaxException;

import javax.validation.Valid;

import com.ryan.temporarycyclechange.controller.resource.GenerateReportDTO;
import com.ryan.temporarycyclechange.service.GenerateReportService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * Controller that handles report generation.
 * 
 * @author jchin13
 */
@RestController
public class ReportController {

    private GenerateReportService service;

    public ReportController(GenerateReportService service) {
        this.service = service;
    }

    @PostMapping("/rest/report")
    public ResponseEntity<?> getReportRunDateFromAndTo(@Valid @RequestBody GenerateReportDTO reportDTO) throws URISyntaxException {
            
            service.generateReport(reportDTO);

            return ResponseEntity.noContent().build();
    }
}