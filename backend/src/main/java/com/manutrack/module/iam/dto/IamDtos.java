//package com.manutrack.module.iam.dto;
//
//import com.manutrack.module.iam.entity.User;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Pattern;
//import jakarta.validation.constraints.Size;
//import lombok.Data;
//
//import java.time.LocalDateTime;
//
//public class IamDtos {
//
//    // ---- Auth DTOs ----
//    @Data
//    public static class RegisterRequest {
//
//        @NotBlank(message = "Name is required")
//        private String name;
//
//        @NotNull(message = "Role is required")
//        private User.Role role;
//
//        @Email(message = "Valid email is required")
//        @NotBlank(message = "Email is required")
//        private String email;
//
//        @NotBlank
//        @Size(min = 6, message = "Password must be at least 6 characters")
//        private String password;
//
//        // ✅ Added proper mobile number validation
//        @NotBlank(message = "Mobile number is required")
//        @Pattern(
//                regexp = "^[0-9]{10}$",
//                message = "Mobile number must be exactly 10 digits"
//        )
//        private String phone;
//    }
//
//    @Data
//    public static class LoginRequest {
//        @Email @NotBlank
//        private String email;
//
//        @NotBlank
//        private String password;
//    }
//
//    @Data
//    public static class AuthResponse {
//        private String token;
//        private String tokenType = "Bearer";
//        private UserResponse user;
//
//        public AuthResponse(String token, UserResponse user) {
//            this.token = token;
//            this.user = user;
//        }
//    }
//
//    // ---- User DTOs ----
//    @Data
//    public static class UserResponse {
//        private Long userId;
//        private String name;
//        private User.Role role;
//        private String email;
//        private String phone;
//        private boolean active;
//        private LocalDateTime createdAt;
//    }
//
//    @Data
//    public static class UpdateUserRequest {
//
//        private String name;
//
//        // ✅ Validation for update (phone optional but must be valid if provided)
//        @Pattern(
//                regexp = "^[0-9]{10}$",
//                message = "Mobile number must be exactly 10 digits"
//        )
//        private String phone;
//
//        private User.Role role;
//        private Boolean active;
//    }
//
//    // ---- AuditLog DTOs ----
//    @Data
//    public static class AuditLogResponse {
//        private Long auditId;
//        private Long userId;
//        private String action;
//        private String resource;
//        private LocalDateTime timestamp;
//        private String metadata;
//    }
//}
package com.manutrack.module.iam.dto;
import com.manutrack.module.iam.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

public class IamDtos {

    // ---- Auth DTOs ----
    @Data
    public static class RegisterRequest {

        @NotBlank(message = "Name is required")
        private String name;

        @NotNull(message = "Role is required")
        private User.Role role;

        // ✅ STRICT EMAIL VALIDATION
        @NotBlank(message = "Email is required")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Invalid email id"
        )
        private String email;

        @NotBlank
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        // ✅ STRICT MOBILE NO VALIDATION
        @NotBlank(message = "Mobile number is required")
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Mobile number must be exactly 10 digits"
        )
        private String phone;
    }

    @Data
    public static class LoginRequest {

        @NotBlank(message = "Email is required")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Invalid email id"
        )
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String tokenType = "Bearer";
        private UserResponse user;

        public AuthResponse(String token, UserResponse user) {
            this.token = token;
            this.user = user;
        }
    }

    // ---- User DTOs ----
    @Data
    public static class UserResponse {
        private Long userId;
        private String name;
        private User.Role role;
        private String email;
        private String phone;
        private boolean active;
        private LocalDateTime createdAt;
    }

    @Data
    public static class UpdateUserRequest {

        private String name;

        // ✅ Email optional but must be valid if provided
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Invalid email id"
        )
        private String email;

        // ✅ Phone optional but checked if present
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Mobile number must be exactly 10 digits"
        )
        private String phone;

        private User.Role role;
        private Boolean active;
    }

    // ---- AuditLog DTOs ----
    @Data
    public static class AuditLogResponse {
        private Long auditId;
        private Long userId;
        private String action;
        private String resource;
        private LocalDateTime timestamp;
        private String metadata;
    }
}
