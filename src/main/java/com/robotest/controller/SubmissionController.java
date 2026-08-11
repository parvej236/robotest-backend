package com.robotest.controller;

import com.robotest.dto.request.SubmissionRequest;
import com.robotest.dto.response.ApiResponse;
import com.robotest.service.SubmissionService;
import jakarta.validation.Valid;
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

    @PostMapping("/contest/{contestId}/question/{questionId}")
    public ResponseEntity<ApiResponse<String>> submit(
            @PathVariable Long contestId,
            @PathVariable Long questionId,
            @Valid @RequestBody SubmissionRequest request, // Expecting {"answer": "12.5"}
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                submissionService.submitSingleAnswer(
                        contestId,
                        userDetails.getUsername(),
                        questionId,
                        request.getAnswer(),
                        request.getQuestionStartedAt()
                ));
    }

    @PostMapping("/contest/{contestId}/complete")
    public ResponseEntity<ApiResponse<String>> completeContest(
            @PathVariable Long contestId,
            @AuthenticationPrincipal UserDetails userDetails) {
        submissionService.completeContest(contestId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Contest marked as completed."));
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