package com.manutrack.module.procurement.dto;

import com.manutrack.module.procurement.entity.Invoice;
import com.manutrack.module.procurement.entity.PurchaseOrder;
import com.manutrack.module.procurement.entity.Vendor;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProcurementDtos {

    @Data public static class CreateVendorRequest {
        @NotBlank private String name;
        private String contactInfo;
        private Double rating;
        private Vendor.Status status;
    }
    @Data public static class UpdateVendorRequest {
        private String name; private String contactInfo;
        private Double rating; private Vendor.Status status;
    }
    @Data public static class VendorResponse {
        private Long vendorId; private String name;
        private String contactInfo; private Double rating;
        private Vendor.Status status; private LocalDateTime createdAt;
    }

    @Data public static class CreatePORequest {
        @NotNull private Long vendorId;
        @NotNull private Long itemId;
        @NotNull @Positive private Double quantity;
        @NotNull @Positive private Double unitPrice;
        private LocalDate orderDate;
        private LocalDate expectedDeliveryDate;
    }
    @Data public static class UpdatePORequest {
        private Double quantity; private Double unitPrice;
        private LocalDate expectedDeliveryDate;
        private PurchaseOrder.Status status;
    }
    @Data public static class POResponse {
        private Long poId; private Long vendorId;
        private String vendorName; private Long itemId;
        private Double quantity; private Double unitPrice;
        private Double totalAmount;
        private LocalDate orderDate; private LocalDate expectedDeliveryDate;
        private PurchaseOrder.Status status; private LocalDateTime createdAt;
    }

    @Data public static class CreateInvoiceRequest {
        @NotNull private Long poId;
        @NotNull @Positive private Double amount;
        private LocalDate issueDate; private LocalDate dueDate;
    }
    @Data public static class UpdateInvoiceRequest {
        private Double amount; private LocalDate dueDate;
        private Invoice.Status status;
    }
    @Data public static class InvoiceResponse {
        private Long invoiceId; private Long poId;
        private Long vendorId; private String vendorName;
        private Double amount; private LocalDate issueDate;
        private LocalDate dueDate; private Invoice.Status status;
        private LocalDateTime createdAt;
    }
}
