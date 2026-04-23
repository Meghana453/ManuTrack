package com.manutrack.module.logistics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity @Table(name = "routes")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Route {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long routeId;
    @Column(nullable = false) private String origin;
    @Column(nullable = false) private String destination;
    private Double distance;
    private Integer estimatedTimeHours;
    @CreatedDate @Column(updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate private LocalDateTime updatedAt;
}
