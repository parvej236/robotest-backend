package com.robotest.controller;

import com.robotest.dto.response.ApiResponse;
import com.robotest.dto.response.LeaderboardEntryDto;
import com.robotest.entity.Result;
import com.robotest.repository.ResultRepository;
import com.robotest.service.LeaderboardService;
import com.robotest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final ResultRepository   resultRepository;
    private final UserService        userService;

    @GetMapping("/contest/{contestId}")
    public ResponseEntity<ApiResponse<List<LeaderboardEntryDto>>> getLeaderboard(
            @PathVariable Long contestId) {
        return ResponseEntity.ok(leaderboardService.getLeaderboard(contestId));
    }

    @GetMapping("/contest/{contestId}/my-result")
    public ResponseEntity<ApiResponse<Object>> getMyResult(
            @PathVariable Long contestId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                leaderboardService.getMyResult(contestId, userDetails.getUsername()));
    }

    // NOTE: The generic type below should be List<Result> — replace RESULT with Result when you copy this
    @GetMapping("/my-results")
    public ResponseEntity<ApiResponse<List<Result>>> getMyResults(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        List<Result> results = resultRepository.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Results fetched", results));
    }
}