package com.manutrack.module.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity @Table(name = "operational_reports")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OperationalReport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long reportId;
    @Column(nullable = false) private String scope;
    @Column(columnDefinition = "TEXT") private String metrics;
    @CreatedDate @Column(updatable = false) private LocalDateTime generatedDate;
}
