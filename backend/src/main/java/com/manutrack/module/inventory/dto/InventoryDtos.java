package com.manutrack.module.inventory.dto;

import com.manutrack.module.inventory.entity.InventoryItem;
import com.manutrack.module.inventory.entity.MaterialRequest;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class InventoryDtos {

    @Data
    public static class CreateItemRequest {
        @NotNull private InventoryItem.ItemType itemType;
        @NotBlank private String description;
        @NotBlank private String unitOfMeasure;
        @NotNull @PositiveOrZero private Double currentStock;
        @NotNull @Positive private Double reorderLevel;
        private Long warehouseId;
    }

    @Data
    public static class UpdateItemRequest {
        private String description;
        private String unitOfMeasure;
        private Double currentStock;
        private Double reorderLevel;
        private Long warehouseId;
        private InventoryItem.Status status;
    }

    @Data
    public static class AdjustStockRequest {
        @NotNull private Double quantity;
        @NotBlank private String reason;
    }

    @Data
    public static class ItemResponse {
        private Long itemId;
        private InventoryItem.ItemType itemType;
        private String description;
        private String unitOfMeasure;
        private Double currentStock;
        private Double reorderLevel;
        private Long warehouseId;
        private InventoryItem.Status status;
        private LocalDateTime createdAt;
    }

    @Data
    public static class CreateMaterialRequestDto {
        private Long workOrderId;
        @NotNull private Long itemId;
        @NotNull @Positive private Double quantity;
        private LocalDate requestedDate;
    }

    @Data
    public static class UpdateMaterialRequestDto {

        private LocalDate requestedDate;

        private Long workOrderId;

        private Long itemId;

        private Double quantity;
        private MaterialRequest.Status status;
    }

    @Data
    public static class MaterialRequestResponse {
        private Long requestId;
        private Long workOrderId;
        private Long itemId;
        private String itemDescription;
        private Double quantity;
        private LocalDate requestedDate;
        private MaterialRequest.Status status;
        private LocalDateTime createdAt;
    }
}
