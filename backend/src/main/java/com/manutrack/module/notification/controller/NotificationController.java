////package com.manutrack.module.notification.controller;
////
////import com.manutrack.module.notification.dto.NotificationDtos;
////import com.manutrack.module.notification.service.NotificationService;
////import io.swagger.v3.oas.annotations.Operation;
////import io.swagger.v3.oas.annotations.tags.Tag;
////import jakarta.validation.Valid;
////import lombok.RequiredArgsConstructor;
////import org.springframework.http.HttpStatus;
////import org.springframework.http.ResponseEntity;
////import org.springframework.security.access.prepost.PreAuthorize;
////import org.springframework.web.bind.annotation.*;
////import java.util.List;
////import java.util.Map;
////
////@RestController @RequestMapping("/notifications")
////@RequiredArgsConstructor @Tag(name = "Notifications & Alerts")
////public class NotificationController {
////
////    private final NotificationService notificationService;
////
////    @PostMapping @PreAuthorize("hasRole('ADMIN')")
////    @Operation(summary = "Create notification")
////    public ResponseEntity<NotificationDtos.NotificationResponse> create(@Valid @RequestBody NotificationDtos.CreateNotificationRequest req) {
////        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(req));
////    }
////
////    @GetMapping @PreAuthorize("hasRole('ADMIN')")
////    @Operation(summary = "Get all notifications (Admin)")
////    public ResponseEntity<List<NotificationDtos.NotificationResponse>> getAll() {
////        return ResponseEntity.ok(notificationService.getAllNotifications());
////    }
////
////    @GetMapping("/user/{userId}")
////    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
////    @Operation(summary = "Get notifications for a user")
////    public ResponseEntity<List<NotificationDtos.NotificationResponse>> getByUser(@PathVariable Long userId) {
////        return ResponseEntity.ok(notificationService.getByUser(userId));
////    }
////
////    @GetMapping("/user/{userId}/unread")
////    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
////    @Operation(summary = "Get unread notifications for a user")
////    public ResponseEntity<List<NotificationDtos.NotificationResponse>> getUnread(@PathVariable Long userId) {
////        return ResponseEntity.ok(notificationService.getUnreadByUser(userId));
////    }
////
////    @GetMapping("/user/{userId}/unread-count")
////    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
////    @Operation(summary = "Count unread notifications")
////    public ResponseEntity<Map<String, Long>> countUnread(@PathVariable Long userId) {
////        return ResponseEntity.ok(Map.of("unreadCount", notificationService.countUnread(userId)));
////    }
////
////    @PatchMapping("/{id}/read")
////    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
////    @Operation(summary = "Mark notification as read")
////    public ResponseEntity<NotificationDtos.NotificationResponse> markRead(@PathVariable Long id) {
////        return ResponseEntity.ok(notificationService.markAsRead(id));
////    }
////
////    @PatchMapping("/{id}/dismiss")
////    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
////    @Operation(summary = "Dismiss notification")
////    public ResponseEntity<NotificationDtos.NotificationResponse> dismiss(@PathVariable Long id) {
////        return ResponseEntity.ok(notificationService.dismiss(id));
////    }
////
////    @PatchMapping("/user/{userId}/mark-all-read")
////    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
////    @Operation(summary = "Mark all notifications as read for user")
////    public ResponseEntity<Void> markAllRead(@PathVariable Long userId) {
////        notificationService.markAllReadForUser(userId);
////        return ResponseEntity.noContent().build();
////    }
////
////    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
////    @Operation(summary = "Delete notification")
////    public ResponseEntity<Void> delete(@PathVariable Long id) {
////        notificationService.delete(id); return ResponseEntity.noContent().build();
////    }
////}
//package com.manutrack.module.notification.controller;
//
//import com.manutrack.module.notification.dto.NotificationDtos;
//import com.manutrack.module.notification.service.NotificationService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//import java.util.Map;
//
//@RestController @RequestMapping("/notifications")
//@RequiredArgsConstructor @Tag(name = "Notifications & Alerts")
//public class NotificationController {
//
//    private final NotificationService notificationService;
//
//    @PostMapping @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Create notification")
//    public ResponseEntity<NotificationDtos.NotificationResponse> create(@Valid @RequestBody NotificationDtos.CreateNotificationRequest req) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(req));
//    }
//
//    @GetMapping @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Get all notifications (Admin)")
//    public ResponseEntity<List<NotificationDtos.NotificationResponse>> getAll() {
//        return ResponseEntity.ok(notificationService.getAllNotifications());
//    }
//
//    @GetMapping("/user/{userId}")
//    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
//    @Operation(summary = "Get notifications for a user")
//    public ResponseEntity<List<NotificationDtos.NotificationResponse>> getByUser(@PathVariable Long userId) {
//        return ResponseEntity.ok(notificationService.getByUser(userId));
//    }
//
//    @GetMapping("/user/{userId}/unread")
//    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
//    @Operation(summary = "Get unread notifications for a user")
//    public ResponseEntity<List<NotificationDtos.NotificationResponse>> getUnread(@PathVariable Long userId) {
//        return ResponseEntity.ok(notificationService.getUnreadByUser(userId));
//    }
//
//    @GetMapping("/user/{userId}/unread-count")
//    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
//    @Operation(summary = "Count unread notifications")
//    public ResponseEntity<Map<String, Long>> countUnread(@PathVariable Long userId) {
//        return ResponseEntity.ok(Map.of("unreadCount", notificationService.countUnread(userId)));
//    }
//
//    @PatchMapping("/{id}/read")
//    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
//    @Operation(summary = "Mark notification as read")
//    public ResponseEntity<NotificationDtos.NotificationResponse> markRead(@PathVariable Long id) {
//        return ResponseEntity.ok(notificationService.markAsRead(id));
//    }
//
//    @PatchMapping("/{id}/dismiss")
//    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
//    @Operation(summary = "Dismiss notification")
//    public ResponseEntity<NotificationDtos.NotificationResponse> dismiss(@PathVariable Long id) {
//        return ResponseEntity.ok(notificationService.dismiss(id));
//    }
//
//    @PatchMapping("/user/{userId}/mark-all-read")
//    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
//    @Operation(summary = "Mark all notifications as read for user")
//    public ResponseEntity<Void> markAllRead(@PathVariable Long userId) {
//        notificationService.markAllReadForUser(userId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Delete notification")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        notificationService.delete(id); return ResponseEntity.noContent().build();
//    }
//}
package com.manutrack.module.notification.controller;

import com.manutrack.module.notification.dto.NotificationDtos;
import com.manutrack.module.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications & Alerts")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create notification")
    public ResponseEntity<NotificationDtos.NotificationResponse> create(
            @Valid @RequestBody NotificationDtos.CreateNotificationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(req));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all notifications (Admin)")
    public ResponseEntity<List<NotificationDtos.NotificationResponse>> getAll() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
    @Operation(summary = "Get notifications for a user")
    public ResponseEntity<List<NotificationDtos.NotificationResponse>> getByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getByUser(userId));
    }

    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
    @Operation(summary = "Get unread notifications for a user")
    public ResponseEntity<List<NotificationDtos.NotificationResponse>> getUnread(
            @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadByUser(userId));
    }

    @GetMapping("/user/{userId}/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
    @Operation(summary = "Count unread notifications")
    public ResponseEntity<Map<String, Long>> countUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("unreadCount", notificationService.countUnread(userId)));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<NotificationDtos.NotificationResponse> markRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PatchMapping("/{id}/dismiss")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
    @Operation(summary = "Dismiss notification")
    public ResponseEntity<NotificationDtos.NotificationResponse> dismiss(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.dismiss(id));
    }

    @PatchMapping("/user/{userId}/mark-all-read")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR','INVENTORY','PROCUREMENT','LOGISTICS')")
    @Operation(summary = "Mark all notifications as read for user")
    public ResponseEntity<Void> markAllRead(@PathVariable Long userId) {
        notificationService.markAllReadForUser(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete notification")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
