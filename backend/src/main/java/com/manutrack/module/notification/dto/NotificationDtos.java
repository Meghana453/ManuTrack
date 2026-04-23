////package com.manutrack.module.notification.dto;
////
////import com.manutrack.module.notification.entity.Notification;
////import jakarta.validation.constraints.NotBlank;
////import jakarta.validation.constraints.NotNull;
////import lombok.Data;
////import java.time.LocalDateTime;
////
////public class NotificationDtos {
////    @Data public static class CreateNotificationRequest {
////        @NotNull private Long userId;
////        @NotBlank private String message;
////        private Notification.Category category;
////    }
////    @Data public static class NotificationResponse {
////        private Long notificationId; private Long userId;
////        private String message; private Notification.Category category;
////        private Notification.Status status; private LocalDateTime createdDate;
////    }
////}
//package com.manutrack.module.notification.dto;
//
//import com.manutrack.module.notification.entity.Notification;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import lombok.Data;
//import java.time.LocalDateTime;
//
//public class NotificationDtos {
//    @Data public static class CreateNotificationRequest {
//        @NotNull private Long userId;
//        @NotBlank private String message;
//        private Notification.Category category;
//    }
//    @Data public static class NotificationResponse {
//        private Long notificationId; private Long userId;
//        private String message; private Notification.Category category;
//        private Notification.Status status; private LocalDateTime createdDate;
//    }
//
//}
package com.manutrack.module.notification.dto;

import com.manutrack.module.notification.entity.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

public class NotificationDtos {

    @Data
    public static class CreateNotificationRequest {
        @NotNull private Long userId;
        @NotBlank private String message;
        private Notification.Category category;
    }

    @Data
    public static class NotificationResponse {
        private Long notificationId;
        private Long userId;
        private String message;
        private Notification.Category category;
        private Notification.Status status;
        private LocalDateTime createdDate;
    }
}
