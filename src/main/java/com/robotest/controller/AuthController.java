package com.robotest.controller;

import com.robotest.dto.request.*;
import com.robotest.dto.response.ApiResponse;
import com.robotest.dto.response.AuthResponse;
import com.robotest.dto.response.UserResponse;
import com.robotest.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Body: { fullName, username, email, password, confirmPassword }
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    /**
     * GET /api/auth/verify-email?token=<uuid>
     * Sent via link in email
     */
    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    /**
     * POST /api/auth/resend-verification?email=<email>
     * Resend verification link if expired
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<String>> resendVerification(
            @RequestParam String email) {
        return ResponseEntity.ok(authService.resendVerification(email));
    }

    /**
     * POST /api/auth/login
     * Body: { email, password }
     * Returns: { accessToken, refreshToken, tokenType, expiresIn, user }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * POST /api/auth/refresh-token
     * Body: { refreshToken }
     * Returns new access + refresh token pair (rotation)
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    /**
     * POST /api/auth/logout
     * Header: Authorization: Bearer <accessToken>
     * Revokes refresh token server-side
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.logout(userDetails.getUsername()));
    }

    /**
     * POST /api/auth/forgot-password
     * Body: { email }
     * Always returns 200 to prevent email enumeration
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    /**
     * POST /api/auth/reset-password
     * Body: { token, newPassword, confirmPassword }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    /**
     * POST /api/auth/change-password
     * Header: Authorization: Bearer <accessToken>
     * Body: { currentPassword, newPassword, confirmPassword }
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                authService.changePassword(userDetails.getUsername(), request));
    }

    /**
     * GET /api/auth/me
     * Header: Authorization: Bearer <accessToken>
     * Returns current authenticated user info
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getCurrentUser(userDetails.getUsername()));
    }

    /**
     * GET /api/auth/validate-token?token=<jwt>
     * Returns 200 if valid, 401 if not
     */
    @GetMapping("/validate-token")
    public ResponseEntity<ApiResponse<String>> validateToken(@RequestParam String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }
}
