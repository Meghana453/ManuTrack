package com.manutrack.module.production.dto;

import com.manutrack.module.production.entity.Machine;
import com.manutrack.module.production.entity.ProductionPlan;
import com.manutrack.module.production.entity.WorkOrder;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProductionDtos {

    // ---- ProductionPlan ----
    @Data
    public static class CreatePlanRequest {
        @NotNull private Long plantId;
        @NotBlank private String planName;
        @NotNull private LocalDate startDate;
        @NotNull private LocalDate endDate;
        @NotNull @Min(1) private Integer targetUnits;
        private ProductionPlan.Status status;
    }

    @Data
    public static class UpdatePlanRequest {
        private String planName;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer targetUnits;
        private ProductionPlan.Status status;
    }

    @Data
    public static class PlanResponse {
        private Long planId;
        private Long plantId;
        private String planName;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer targetUnits;
        private ProductionPlan.Status status;
        private LocalDateTime createdAt;
    }

    // ---- Machine ----
    @Data
    public static class CreateMachineRequest {
        @NotNull private Long plantId;
        @NotBlank private String name;
        private Integer capacity;
        private Machine.Status status;
    }

    @Data
    public static class UpdateMachineRequest {
        private String name;
        private Integer capacity;
        private Machine.Status status;
    }

    @Data
    public static class MachineResponse {
        private Long machineId;
        private Long plantId;
        private String name;
        private Integer capacity;
        private Machine.Status status;
        private LocalDateTime createdAt;
    }

    // ---- WorkOrder ----
    @Data
    public static class CreateWorkOrderRequest {
        @NotNull private Long planId;
        private Long machineId;
        @NotBlank private String productId;
        @NotNull @Min(1) private Integer quantity;
        private LocalDateTime scheduledStart;
        private LocalDateTime scheduledEnd;
        private WorkOrder.Status status;
    }

    @Data
    public static class UpdateWorkOrderRequest {
        private Long machineId;
        private Integer quantity;
        private LocalDateTime scheduledStart;
        private LocalDateTime scheduledEnd;
        private LocalDateTime actualStart;
        private LocalDateTime actualEnd;
        private WorkOrder.Status status;
    }

    @Data
    public static class WorkOrderResponse {
        private Long workOrderId;
        private Long planId;
        private String planName;
        private Long machineId;
        private String machineName;
        private String productId;
        private Integer quantity;
        private LocalDateTime scheduledStart;
        private LocalDateTime scheduledEnd;
        private LocalDateTime actualStart;
        private LocalDateTime actualEnd;
        private WorkOrder.Status status;
        private LocalDateTime createdAt;
    }
}
