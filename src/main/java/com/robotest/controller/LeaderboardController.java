package com.robotest.controller;

import com.robotest.dto.response.ApiResponse;
import com.robotest.dto.response.LeaderboardEntryDto;
import com.robotest.dto.response.ResultResponseDto;
import com.robotest.entity.Result;
import com.robotest.entity.User;
import com.robotest.exception.AppException;
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

//    @GetMapping("/contest/{contestId}/my-result")
//    public ResponseEntity<ApiResponse<Result>> getMyResult(
//            @PathVariable Long contestId,
//            @AuthenticationPrincipal UserDetails userDetails) {
//        // Updated service call to return Result entity with question details
//        return ResponseEntity.ok(leaderboardService.getMyResults(contestId, userDetails.getUsername()));
//    }

    /**
     * Get all historical results for the authenticated user across all contests.
     */
    @GetMapping("/my-history")
    public ResponseEntity<ApiResponse<List<ResultResponseDto>>> getMyResults(
            @AuthenticationPrincipal UserDetails userDetails) {
        // Return the DTO list to prevent "no Session" errors
        List<ResultResponseDto> history = leaderboardService.getMyResults(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("History fetched", history));
    }
}