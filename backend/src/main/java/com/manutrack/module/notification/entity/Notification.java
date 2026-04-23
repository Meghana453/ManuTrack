////package com.manutrack.module.notification.entity;
////
////import jakarta.persistence.*;
////import lombok.*;
////import org.springframework.data.annotation.CreatedDate;
////import org.springframework.data.jpa.domain.support.AuditingEntityListener;
////import java.time.LocalDateTime;
////
////@Entity @Table(name = "notifications")
////@EntityListeners(AuditingEntityListener.class)
////@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
////public class Notification {
////    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long notificationId;
////    @Column(nullable = false) private Long userId;
////    @Column(nullable = false, columnDefinition = "TEXT") private String message;
////    @Enumerated(EnumType.STRING) private Category category;
////    @Enumerated(EnumType.STRING) @Builder.Default private Status status = Status.UNREAD;
////    @CreatedDate @Column(updatable = false) private LocalDateTime createdDate;
////
////    public enum Category { INVENTORY, SHIPMENT, MACHINE, WORK_ORDER, PROCUREMENT, GENERAL }
////    public enum Status { UNREAD, READ, DISMISSED }
////}
//package com.manutrack.module.notification.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//import java.time.LocalDateTime;
//
//@Entity @Table(name = "notifications")
//@EntityListeners(AuditingEntityListener.class)
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//public class Notification {
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long notificationId;
//    @Column(nullable = false) private Long userId;
//    @Column(nullable = false, columnDefinition = "TEXT") private String message;
//    @Enumerated(EnumType.STRING) private Category category;
//    @Enumerated(EnumType.STRING) @Builder.Default private Status status = Status.UNREAD;
//    @CreatedDate @Column(updatable = false) private LocalDateTime createdDate;
//
//    public enum Category { INVENTORY, SHIPMENT, MACHINE, WORK_ORDER, PROCUREMENT, GENERAL }
//    public enum Status { UNREAD, READ, DISMISSED }
//}
package com.manutrack.module.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.UNREAD;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    public enum Category {
        INVENTORY, SHIPMENT, MACHINE, WORK_ORDER, PROCUREMENT, GENERAL
    }

    public enum Status {
        UNREAD, READ, DISMISSED
    }
}
