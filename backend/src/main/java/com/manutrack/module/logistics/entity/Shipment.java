package com.manutrack.module.logistics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "shipments")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shipment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long shipmentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id") private Carrier carrier;
    private Long originWarehouseId;
    @Column(nullable = false) private String destination;
    private LocalDate scheduledDate;
    private LocalDate actualDispatchDate;
    private LocalDate deliveryDate;
    @Enumerated(EnumType.STRING) @Builder.Default private Status status = Status.SCHEDULED;
    @CreatedDate @Column(updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate private LocalDateTime updatedAt;
    public enum Status { SCHEDULED, IN_TRANSIT, DELIVERED, DELAYED, CANCELLED }
}
