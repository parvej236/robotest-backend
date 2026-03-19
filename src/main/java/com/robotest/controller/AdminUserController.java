package com.robotest.controller;

import com.robotest.dto.request.AdminCreateUserRequest;
import com.robotest.dto.request.AdminUpdateUserRequest;
import com.robotest.dto.response.ApiResponse;
import com.robotest.dto.response.UserProfileResponse;
import com.robotest.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")   // entire controller requires ADMIN role
public class AdminUserController {

    private final AdminUserService adminUserService;

    // GET /api/users  — list all users
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    // GET /api/users/{id}  — single user
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.getUserById(id));
    }

    // POST /api/users  — create user
    @PostMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> createUser(
            @Valid @RequestBody AdminCreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminUserService.createUser(request));
    }

    // PUT /api/users/{id}  — update user
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUser(
            @PathVariable Long id,
            @RequestBody AdminUpdateUserRequest request) {
        return ResponseEntity.ok(adminUserService.updateUser(id, request));
    }

    // DELETE /api/users/{id}  — delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.deleteUser(id));
    }
}