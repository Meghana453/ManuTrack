package com.manutrack.module.analytics.controller;

import com.manutrack.module.analytics.dto.AnalyticsDtos;
import com.manutrack.module.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/analytics")
@RequiredArgsConstructor @Tag(name = "Operational Analytics & Reporting")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
    @Operation(summary = "Get full dashboard summary")
    public ResponseEntity<AnalyticsDtos.DashboardSummary> getDashboard() {
        return ResponseEntity.ok(analyticsService.getDashboardSummary());
    }

    @GetMapping("/production")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR')")
    @Operation(summary = "Get production analytics")
    public ResponseEntity<AnalyticsDtos.ProductionSummary> getProduction() {
        return ResponseEntity.ok(analyticsService.getProductionSummary());
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY')")
    @Operation(summary = "Get inventory analytics")
    public ResponseEntity<AnalyticsDtos.InventorySummary> getInventory() {
        return ResponseEntity.ok(analyticsService.getInventorySummary());
    }

    @GetMapping("/procurement")
    @PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT')")
    @Operation(summary = "Get procurement analytics")
    public ResponseEntity<AnalyticsDtos.ProcurementSummary> getProcurement() {
        return ResponseEntity.ok(analyticsService.getProcurementSummary());
    }

    @GetMapping("/logistics")
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
    @Operation(summary = "Get logistics analytics")
    public ResponseEntity<AnalyticsDtos.LogisticsSummary> getLogistics() {
        return ResponseEntity.ok(analyticsService.getLogisticsSummary());
    }

    @PostMapping("/reports/generate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generate and save operational report")
    public ResponseEntity<AnalyticsDtos.ReportResponse> generateReport(@RequestParam(defaultValue = "FULL") String scope) {
        return ResponseEntity.ok(analyticsService.generateReport(scope));
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all saved reports")
    public ResponseEntity<List<AnalyticsDtos.ReportResponse>> getAllReports() {
        return ResponseEntity.ok(analyticsService.getAllReports());
    }
}
