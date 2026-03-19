package com.robotest.service;

import com.robotest.dto.request.UpdateProfileRequest;
import com.robotest.dto.response.ApiResponse;
import com.robotest.dto.response.UserProfileResponse;
import com.robotest.entity.User;
import com.robotest.exception.AppException;
import com.robotest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository     userRepository;
    private final FileStorageService fileStorageService;

    // ── Public helper used by other services ──────────────────
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.notFound("User not found: " + email));
    }

    // ── GET PROFILE ───────────────────────────────────────────
    public ApiResponse<UserProfileResponse> getProfile(String email) {
        return ApiResponse.success("Profile fetched", toResponse(findByEmail(email)));
    }

    // ── UPDATE PROFILE ────────────────────────────────────────
    @Transactional
    public ApiResponse<UserProfileResponse> updateProfile(String email, UpdateProfileRequest req) {
        User user = findByEmail(email);
        if (req.getFullName()           != null) user.setFullName(req.getFullName().trim());
        if (req.getGender()             != null) user.setGender(req.getGender());
        if (req.getRegistrationNumber() != null) user.setRegistrationNumber(req.getRegistrationNumber().trim());
        if (req.getRollNumber()         != null) user.setRollNumber(req.getRollNumber().trim());
        if (req.getUniversity()         != null) user.setUniversity(req.getUniversity().trim());
        if (req.getHobby()              != null) user.setHobby(req.getHobby().trim());
        if (req.getBio()                != null) user.setBio(req.getBio().trim());
        return ApiResponse.success("Profile updated", toResponse(userRepository.save(user)));
    }

    // ── UPLOAD AVATAR ─────────────────────────────────────────
    @Transactional
    public ApiResponse<UserProfileResponse> uploadAvatar(String email, MultipartFile file) {
        User user = findByEmail(email);
        if (user.getProfileImageUrl() != null)
            fileStorageService.delete(user.getProfileImageUrl());
        user.setProfileImageUrl(fileStorageService.storeFile(file, "profiles"));
        return ApiResponse.success("Avatar uploaded", toResponse(userRepository.save(user)));
    }

    // ── DELETE AVATAR ─────────────────────────────────────────
    @Transactional
    public ApiResponse<UserProfileResponse> deleteAvatar(String email) {
        User user = findByEmail(email);
        if (user.getProfileImageUrl() != null) {
            fileStorageService.delete(user.getProfileImageUrl());
            user.setProfileImageUrl(null);
            userRepository.save(user);
        }
        return ApiResponse.success("Avatar removed", toResponse(user));
    }

    // ── MAPPER ────────────────────────────────────────────────
    public UserProfileResponse toResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .gender(user.getGender())
                .registrationNumber(user.getRegistrationNumber())
                .rollNumber(user.getRollNumber())
                .university(user.getUniversity())
                .hobby(user.getHobby())
                .bio(user.getBio())
                .enabled(user.isEnabled())
                .emailVerified(user.isEmailVerified())
                .roles(user.getRoles().stream()
                        .map(r -> r.getName().name())
                        .collect(java.util.stream.Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}