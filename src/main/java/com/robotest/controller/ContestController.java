package com.robotest.controller;

import com.robotest.dto.request.ContestRequest;
import com.robotest.dto.response.ApiResponse;
import com.robotest.dto.response.ContestDto;
import com.robotest.dto.response.QuestionDto;
import com.robotest.service.ContestService;
import com.robotest.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ContestController {

    private final ContestService  contestService;
    private final QuestionService questionService;

    // ── Public endpoints ──────────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContestDto>>> getAll() {
        return ResponseEntity.ok(contestService.getAllContests());
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ContestDto>>> getActive() {
        return ResponseEntity.ok(contestService.getActiveContests());
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<List<ContestDto>>> getLatest() {
        return ResponseEntity.ok(contestService.getLatestContests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContestDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contestService.getContestById(id));
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<ApiResponse<List<QuestionDto>>> getQuestions(
            @PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionsForUser(id));
    }

    // ── Authenticated user endpoints ──────────────────────────

    @PostMapping("/{id}/register")
    public ResponseEntity<ApiResponse<String>> register(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                contestService.registerForContest(id, userDetails.getUsername()));
    }

    @GetMapping("/{id}/is-registered")
    public ResponseEntity<ApiResponse<Boolean>> isRegistered(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                contestService.isRegistered(id, userDetails.getUsername()));
    }

    @GetMapping("/my-contests")
    public ResponseEntity<ApiResponse<List<ContestDto>>> myContests(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                contestService.getUserContests(userDetails.getUsername()));
    }

    // ── Admin endpoints ───────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ContestDto>> create(
            @Valid @RequestBody ContestRequest request) {
        return ResponseEntity.ok(contestService.createContest(request));
    }

    @PostMapping("/{contestId}/send-announcement")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> sendContestAnnouncement(
            @PathVariable Long contestId) {
        contestService.sendContestAnnouncementEmail(contestId);
        return ResponseEntity.ok(ApiResponse.success("Emails sent successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ContestDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody ContestRequest request) {
        return ResponseEntity.ok(contestService.updateContest(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(contestService.deleteContest(id));
    }
}