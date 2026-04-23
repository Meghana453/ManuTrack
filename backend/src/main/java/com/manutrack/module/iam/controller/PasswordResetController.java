package com.manutrack.module.iam.controller;

import com.manutrack.module.iam.dto.PasswordResetDtos;
import com.manutrack.module.iam.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Password Reset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/auth/forgot-password")
    @Operation(summary = "Request password reset - prints link to terminal")
    public ResponseEntity<PasswordResetDtos.MessageResponse> forgotPassword(
            @Valid @RequestBody PasswordResetDtos.ForgotPasswordRequest request) {
        return ResponseEntity.ok(passwordResetService.requestPasswordReset(request.getEmail()));
    }

    @GetMapping("/auth/validate-reset-token")
    @Operation(summary = "Validate reset token before showing reset form")
    public ResponseEntity<PasswordResetDtos.MessageResponse> validateToken(
            @RequestParam String token) {
        return ResponseEntity.ok(passwordResetService.validateToken(token));
    }

    @PostMapping("/auth/reset-password")
    @Operation(summary = "Reset password using token")
    public ResponseEntity<PasswordResetDtos.MessageResponse> resetPassword(
            @Valid @RequestBody PasswordResetDtos.ResetPasswordRequest request) {
        return ResponseEntity.ok(passwordResetService.resetPassword(request));
    }
}
