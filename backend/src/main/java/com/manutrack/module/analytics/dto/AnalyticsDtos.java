package com.manutrack.module.analytics.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.Map;

public class AnalyticsDtos {

    @Data @Builder
    public static class DashboardSummary {
        private ProductionSummary production;
        private InventorySummary inventory;
        private ProcurementSummary procurement;
        private LogisticsSummary logistics;
        private LocalDateTime generatedAt;
    }

    @Data @Builder
    public static class ProductionSummary {
        private long totalPlans;
        private long inProgressPlans;
        private long completedPlans;
        private long totalWorkOrders;
        private long pendingWorkOrders;
        private long haltedWorkOrders;
        private long totalMachines;
        private long activeMachines;
        private long maintenanceMachines;
    }

    @Data @Builder
    public static class InventorySummary {
        private long totalItems;
        private long availableItems;
        private long lowStockItems;
        private long outOfStockItems;
        private long totalMaterialRequests;
        private long pendingMaterialRequests;
    }

    @Data @Builder
    public static class ProcurementSummary {
        private long totalVendors;
        private long activeVendors;
        private long totalPOs;
        private long openPOs;
        private long totalInvoices;
        private long pendingInvoices;
        private long overdueInvoices;
        private double totalPOValue;
    }

    @Data @Builder
    public static class LogisticsSummary {
        private long totalShipments;
        private long scheduledShipments;
        private long inTransitShipments;
        private long deliveredShipments;
        private long delayedShipments;
        private long totalCarriers;
        private long totalRoutes;
    }

    @Data
    public static class ReportRequest {
        private String scope;
    }

    @Data @Builder
    public static class ReportResponse {
        private Long reportId;
        private String scope;
        private String metrics;
        private LocalDateTime generatedDate;
    }
}
