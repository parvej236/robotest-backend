package com.robotest.controller;

import com.robotest.dto.request.UpdateProfileRequest;
import com.robotest.dto.response.ApiResponse;
import com.robotest.dto.response.UserProfileResponse;
import com.robotest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/users/me
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }

    // PUT /api/users/me
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                userService.updateProfile(userDetails.getUsername(), request));
    }

    // POST /api/users/me/avatar  (multipart/form-data, field name: "file")
    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserProfileResponse>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                userService.uploadAvatar(userDetails.getUsername(), file));
    }

    // DELETE /api/users/me/avatar
    @DeleteMapping("/me/avatar")
    public ResponseEntity<ApiResponse<UserProfileResponse>> deleteAvatar(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.deleteAvatar(userDetails.getUsername()));
    }
}