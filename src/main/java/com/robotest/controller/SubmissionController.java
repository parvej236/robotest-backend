package com.robotest.controller;

import com.robotest.dto.request.SubmissionRequest;
import com.robotest.dto.response.ApiResponse;
import com.robotest.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/contest/{contestId}")
    public ResponseEntity<ApiResponse<String>> submit(
            @PathVariable Long contestId,
            @RequestBody SubmissionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                submissionService.submitAnswers(contestId, userDetails.getUsername(), request));
    }

    // ─────────────────────────────────────────────
    // Check if user already submitted (IMPORTANT)
    // ─────────────────────────────────────────────
    @GetMapping("/contest/{contestId}/exists")
    public ResponseEntity<Boolean> hasSubmitted(
            @PathVariable Long contestId,
            @AuthenticationPrincipal UserDetails userDetails) {

        boolean exists = submissionService.hasUserSubmitted(
                contestId,
                userDetails.getUsername()
        );

        return ResponseEntity.ok(exists);
    }
}