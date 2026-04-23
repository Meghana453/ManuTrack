package com.manutrack.module.logistics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity @Table(name = "carriers")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Carrier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long carrierId;
    @Column(nullable = false) private String name;
    private String contactInfo;
    @Builder.Default private Double rating = 0.0;
    @Enumerated(EnumType.STRING) @Builder.Default private Status status = Status.ACTIVE;
    @CreatedDate @Column(updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate private LocalDateTime updatedAt;
    public enum Status { ACTIVE, INACTIVE }
}
