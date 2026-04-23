package com.manutrack.module.iam.service;

import com.manutrack.exception.BusinessException;
import com.manutrack.module.iam.dto.PasswordResetDtos;
import com.manutrack.module.iam.entity.PasswordResetToken;
import com.manutrack.module.iam.entity.User;
import com.manutrack.module.iam.repository.PasswordResetTokenRepository;
import com.manutrack.module.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String FRONTEND_URL = "http://localhost:3000";

    /**
     * Step 1: User submits their email.
     * Generates a reset token, prints the link to the IntelliJ terminal.
     */
    public PasswordResetDtos.MessageResponse requestPasswordReset(String email) {
        // Always return success message even if email not found (security best practice)
        userRepository.findByEmail(email).ifPresent(user -> {
            // Delete any existing tokens for this email
            tokenRepository.deleteByEmail(email);

            // Generate a unique token
            String token = UUID.randomUUID().toString();

            // Save token — expires in 30 minutes
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .email(email)
                    .expiresAt(LocalDateTime.now().plusMinutes(30))
                    .used(false)
                    .build();
            tokenRepository.save(resetToken);

            // Print the reset link to IntelliJ terminal
            String resetLink = FRONTEND_URL + "/reset-password?token=" + token;
            log.info("");
            log.info("========================================");
            log.info("  PASSWORD RESET LINK FOR: {}", email);
            log.info("  Click the link below to reset password:");
            log.info("  {}", resetLink);
            log.info("  Link expires in 30 minutes.");
            log.info("========================================");
            log.info("");

            System.out.println("\n>>> PASSWORD RESET LINK for " + email + ":");
            System.out.println(">>> " + resetLink);
            System.out.println(">>> (Link expires in 30 minutes)\n");
        });

        return new PasswordResetDtos.MessageResponse(
                "If an account exists with that email, a reset link has been printed to the server terminal."
        );
    }

    /**
     * Step 2: User clicks the link → lands on /reset-password?token=xxx
     * Validates the token is valid and not expired.
     */
    @Transactional(readOnly = true)
    public PasswordResetDtos.MessageResponse validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Invalid or expired reset link. Please request a new one."));

        if (resetToken.isUsed()) {
            throw new BusinessException("This reset link has already been used. Please request a new one.");
        }
        if (resetToken.isExpired()) {
            throw new BusinessException("This reset link has expired (30 min limit). Please request a new one.");
        }

        return new PasswordResetDtos.MessageResponse("Token is valid. Email: " + resetToken.getEmail());
    }

    /**
     * Step 3: User submits new password on the reset form.
     * Validates token + updates password.
     */
    public PasswordResetDtos.MessageResponse resetPassword(PasswordResetDtos.ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match.");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessException("Invalid or expired reset link."));

        if (resetToken.isUsed()) {
            throw new BusinessException("This reset link has already been used.");
        }
        if (resetToken.isExpired()) {
            throw new BusinessException("This reset link has expired. Please request a new one.");
        }

        // Verify email matches token
        if (!resetToken.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new BusinessException("Email does not match the reset token.");
        }

        // Update the user's password
        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new BusinessException("User account not found."));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("[PASSWORD RESET] Password successfully reset for: {}", user.getEmail());
        System.out.println("\n>>> Password successfully reset for: " + user.getEmail() + "\n");

        return new PasswordResetDtos.MessageResponse("Password has been reset successfully. You can now sign in.");
    }
}
