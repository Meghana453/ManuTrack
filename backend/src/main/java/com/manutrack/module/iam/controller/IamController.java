package com.manutrack.module.iam.controller;

import com.manutrack.module.iam.dto.IamDtos;
import com.manutrack.module.iam.service.IamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Identity & Access Management")
public class IamController {

    private final IamService iamService;

    // ---- Auth endpoints ----
    @PostMapping("/auth/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<IamDtos.AuthResponse> register(@Valid @RequestBody IamDtos.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iamService.register(request));
    }

    @PostMapping("/auth/login")
    @Operation(summary = "Login and receive JWT token")
    public ResponseEntity<IamDtos.AuthResponse> login(@Valid @RequestBody IamDtos.LoginRequest request) {
        return ResponseEntity.ok(iamService.login(request));
    }

    // ---- User management ----
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users (Admin only)")
    public ResponseEntity<List<IamDtos.UserResponse>> getAllUsers() {
        return ResponseEntity.ok(iamService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLANNER', 'SUPERVISOR', 'INVENTORY', 'PROCUREMENT', 'LOGISTICS')")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<IamDtos.UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(iamService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user")
    public ResponseEntity<IamDtos.UserResponse> updateUser(@PathVariable Long id,
                                                            @Valid @RequestBody IamDtos.UpdateUserRequest request) {
        return ResponseEntity.ok(iamService.updateUser(id, request));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        iamService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Audit logs ----
    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all audit logs")
    public ResponseEntity<List<IamDtos.AuditLogResponse>> getAuditLogs() {
        return ResponseEntity.ok(iamService.getAuditLogs());
    }

    @GetMapping("/audit-logs/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs by user")
    public ResponseEntity<List<IamDtos.AuditLogResponse>> getAuditLogsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(iamService.getAuditLogsByUser(userId));
    }
}
