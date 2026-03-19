package com.robotest.service;

import com.robotest.dto.request.AdminCreateUserRequest;
import com.robotest.dto.request.AdminUpdateUserRequest;
import com.robotest.dto.response.ApiResponse;
import com.robotest.dto.response.UserProfileResponse;
import com.robotest.entity.Role;
import com.robotest.entity.User;
import com.robotest.enums.RoleName;
import com.robotest.exception.AppException;
import com.robotest.repository.RoleRepository;
import com.robotest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final UserRepository  userRepository;
    private final RoleRepository  roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService     userService;   // reuse toResponse() mapper

    // ── GET ALL USERS ─────────────────────────────────────────
    public ApiResponse<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> list = userRepository.findAll()
                .stream()
                .map(userService::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.success("Users fetched", list);
    }

    // ── GET SINGLE USER ───────────────────────────────────────
    public ApiResponse<UserProfileResponse> getUserById(Long id) {
        User user = findById(id);
        return ApiResponse.success("User fetched", userService.toResponse(user));
    }

    // ── CREATE USER ───────────────────────────────────────────
    @Transactional
    public ApiResponse<UserProfileResponse> createUser(AdminCreateUserRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw AppException.conflict("Email already registered");
        if (userRepository.existsByUsername(req.getUsername()))
            throw AppException.conflict("Username already taken");

        Set<Role> roles = resolveRoles(req.getRoles());

        User user = User.builder()
                .fullName(req.getFullName())
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .enabled(req.isEnabled())
                .emailVerified(req.isEmailVerified())
                .roles(roles)
                .build();

        User saved = userRepository.save(user);
        log.info("Admin created user: {}", saved.getEmail());
        return ApiResponse.success("User created successfully", userService.toResponse(saved));
    }

    // ── UPDATE USER ───────────────────────────────────────────
    @Transactional
    public ApiResponse<UserProfileResponse> updateUser(Long id, AdminUpdateUserRequest req) {
        User user = findById(id);

        if (req.getFullName()           != null) user.setFullName(req.getFullName().trim());
        if (req.getGender()             != null) user.setGender(req.getGender());
        if (req.getRegistrationNumber() != null) user.setRegistrationNumber(req.getRegistrationNumber().trim());
        if (req.getRollNumber()         != null) user.setRollNumber(req.getRollNumber().trim());
        if (req.getUniversity()         != null) user.setUniversity(req.getUniversity().trim());
        if (req.getHobby()              != null) user.setHobby(req.getHobby().trim());
        if (req.getBio()                != null) user.setBio(req.getBio().trim());
        if (req.getEnabled()            != null) user.setEnabled(req.getEnabled());
        if (req.getEmailVerified()      != null) user.setEmailVerified(req.getEmailVerified());
        if (req.getRoles()              != null && !req.getRoles().isEmpty()) {
            user.setRoles(resolveRoles(req.getRoles()));
        }

        User saved = userRepository.save(user);
        log.info("Admin updated user: {}", saved.getEmail());
        return ApiResponse.success("User updated successfully", userService.toResponse(saved));
    }

    // ── DELETE USER ───────────────────────────────────────────
    @Transactional
    public ApiResponse<String> deleteUser(Long id) {
        User user = findById(id);

        // Prevent deleting admin accounts
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_ADMIN);
        if (isAdmin)
            throw AppException.forbidden("Cannot delete an admin account");

        userRepository.delete(user);
        log.info("Admin deleted user: {}", user.getEmail());
        return ApiResponse.success("User deleted successfully");
    }

    // ── Helpers ───────────────────────────────────────────────
    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("User not found with id: " + id));
    }

    private Set<Role> resolveRoles(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            // Default to ROLE_USER
            return Set.of(roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> AppException.badRequest("ROLE_USER not found")));
        }

        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            try {
                RoleName rn = RoleName.valueOf(roleName);
                Role role = roleRepository.findByName(rn)
                        .orElseThrow(() -> AppException.badRequest("Role not found: " + roleName));
                roles.add(role);
            } catch (IllegalArgumentException e) {
                throw AppException.badRequest("Invalid role: " + roleName + ". Valid: ROLE_USER, ROLE_ADMIN");
            }
        }
        return roles;
    }
}