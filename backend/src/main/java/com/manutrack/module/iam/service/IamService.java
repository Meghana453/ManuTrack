package com.manutrack.module.iam.service;

import com.manutrack.exception.DuplicateResourceException;
import com.manutrack.exception.ResourceNotFoundException;
import com.manutrack.module.iam.dto.IamDtos;
import com.manutrack.module.iam.entity.AuditLog;
import com.manutrack.module.iam.entity.User;
import com.manutrack.module.iam.mapper.IamMapper;
import com.manutrack.module.iam.repository.AuditLogRepository;
import com.manutrack.module.iam.repository.UserRepository;
import com.manutrack.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IamService {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final IamMapper iamMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public IamDtos.AuthResponse register(IamDtos.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }
        User user = User.builder()
                .name(request.getName())
                .role(request.getRole())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .active(true)
                .build();
        user = userRepository.save(user);
        logAudit(user.getUserId(), "REGISTER", "User");
        String token = jwtTokenProvider.generateToken(user);
        return new IamDtos.AuthResponse(token, iamMapper.toUserResponse(user));
    }

    public IamDtos.AuthResponse login(IamDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));
        logAudit(user.getUserId(), "LOGIN", "User");
        String token = jwtTokenProvider.generateToken(user);
        return new IamDtos.AuthResponse(token, iamMapper.toUserResponse(user));
    }

    @Transactional(readOnly = true)
    public List<IamDtos.UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(iamMapper::toUserResponse).toList();
    }

    @Transactional(readOnly = true)
    public IamDtos.UserResponse getUserById(Long id) {
        return iamMapper.toUserResponse(findUserById(id));
    }

    public IamDtos.UserResponse updateUser(Long id, IamDtos.UpdateUserRequest request) {
        User user = findUserById(id);
        iamMapper.updateUserFromRequest(request, user);
        user = userRepository.save(user);
        logAudit(id, "UPDATE", "User");
        return iamMapper.toUserResponse(user);
    }

    public void deleteUser(Long id) {
        findUserById(id);
        userRepository.deleteById(id);
        logAudit(id, "DELETE", "User");
    }

    @Transactional(readOnly = true)
    public List<IamDtos.AuditLogResponse> getAuditLogs() {
        return auditLogRepository.findAll().stream().map(iamMapper::toAuditLogResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<IamDtos.AuditLogResponse> getAuditLogsByUser(Long userId) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream().map(iamMapper::toAuditLogResponse).toList();
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private void logAudit(Long userId, String action, String resource) {
        AuditLog log = AuditLog.builder()
                .userId(userId).action(action).resource(resource).build();
        auditLogRepository.save(log);
    }
}
