package com.manutrack.module.logistics.dto;

import com.manutrack.module.logistics.entity.Carrier;
import com.manutrack.module.logistics.entity.Shipment;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LogisticsDtos {

    @Data public static class CreateCarrierRequest {
        @NotBlank private String name;
        private String contactInfo; private Double rating;
        private Carrier.Status status;
    }
    @Data public static class UpdateCarrierRequest {
        private String name; private String contactInfo;
        private Double rating; private Carrier.Status status;
    }
    @Data public static class CarrierResponse {
        private Long carrierId; private String name;
        private String contactInfo; private Double rating;
        private Carrier.Status status; private LocalDateTime createdAt;
    }

    @Data public static class CreateRouteRequest {
        @NotBlank private String origin;
        @NotBlank private String destination;
        private Double distance; private Integer estimatedTimeHours;
    }
    @Data public static class UpdateRouteRequest {
        private String origin; private String destination;
        private Double distance; private Integer estimatedTimeHours;
    }
    @Data public static class RouteResponse {
        private Long routeId; private String origin;
        private String destination; private Double distance;
        private Integer estimatedTimeHours; private LocalDateTime createdAt;
    }

    @Data public static class CreateShipmentRequest {
        private Long carrierId; private Long originWarehouseId;
        @NotBlank private String destination;
        private LocalDate scheduledDate;
    }
    @Data public static class UpdateShipmentRequest {
        private Long carrierId; private String destination;
        private LocalDate scheduledDate; private LocalDate actualDispatchDate;
        private LocalDate deliveryDate; private Shipment.Status status;
    }
    @Data public static class ShipmentResponse {
        private Long shipmentId; private Long carrierId;
        private String carrierName; private Long originWarehouseId;
        private String destination; private LocalDate scheduledDate;
        private LocalDate actualDispatchDate; private LocalDate deliveryDate;
        private Shipment.Status status; private LocalDateTime createdAt;
    }
}
