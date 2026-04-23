package com.manutrack.module.procurement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_orders")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long poId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;
    @Column(nullable = false) private Long itemId;
    @Column(nullable = false) private Double quantity;
    @Column(nullable = false) private Double unitPrice;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    @Enumerated(EnumType.STRING) @Builder.Default private Status status = Status.OPEN;
    @CreatedDate @Column(updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate private LocalDateTime updatedAt;
    public enum Status { OPEN, DELIVERED, CANCELLED }
}
