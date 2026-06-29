package com.smarterp.controllers;

import com.smarterp.dto.ReportResponse;
import com.smarterp.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<ReportResponse> getReport() {
        try {
            return ResponseEntity.ok(reportService.generateReport());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}