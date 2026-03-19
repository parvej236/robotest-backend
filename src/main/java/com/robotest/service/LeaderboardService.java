package com.robotest.service;

import com.robotest.dto.response.ApiResponse;
import com.robotest.dto.response.LeaderboardEntryDto;
import com.robotest.entity.*;
import com.robotest.exception.AppException;
import com.robotest.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardService {

    private final SubmissionRepository submissionRepository;
    private final QuestionRepository   questionRepository;
    private final UserRepository       userRepository;
    private final ResultRepository     resultRepository;
    private final ContestService       contestService;

    // ── GET LEADERBOARD ───────────────────────────────────────
    public ApiResponse<List<LeaderboardEntryDto>> getLeaderboard(Long contestId) {
        contestService.findById(contestId); // verify exists
        return ApiResponse.success("Leaderboard fetched", buildLeaderboard(contestId));
    }

    // ── GET MY RESULT ─────────────────────────────────────────
    public ApiResponse<Object> getMyResult(Long contestId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.notFound("User not found"));
        Optional<Result> result = resultRepository.findByUserIdAndContestId(user.getId(), contestId);
        return ApiResponse.success("Result fetched", result.orElse(null));
    }

    // ── CALCULATE AND SAVE FINAL RESULTS (called by scheduler) ──
    @Transactional
    public void calculateAndSaveResults(Long contestId) {
        Contest contest = contestService.findById(contestId);
        List<LeaderboardEntryDto> lb = buildLeaderboard(contestId);

        for (LeaderboardEntryDto entry : lb) {
            User user = userRepository.findById(entry.getUserId()).orElse(null);
            if (user == null) continue;

            Optional<Result> existing = resultRepository.findByUserIdAndContestId(user.getId(), contestId);
            Result result = existing.orElse(Result.builder().user(user).contest(contest).build());
            result.setCorrectCount(entry.getCorrectCount());
            result.setTotalQuestions(entry.getTotalQuestions());
            result.setRank(entry.getRank());
            result.setLastSubmissionTime(entry.getLastSubmissionTime());
            resultRepository.save(result);
        }
        log.info("Results calculated for contest {}", contestId);
    }

    // ── Private: build leaderboard from raw submissions ───────
    private List<LeaderboardEntryDto> buildLeaderboard(Long contestId) {
        List<Object[]> raw = submissionRepository.findLeaderboardData(contestId);
        int total = (int) questionRepository.countByContestId(contestId);

        List<LeaderboardEntryDto> entries = new ArrayList<>();
        int rank = 1;
        for (Object[] row : raw) {
            Long          userId       = (Long)          row[0];
            long          correctCount = (long)          row[1];
            LocalDateTime lastTime     = (LocalDateTime) row[2];

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) continue;

            entries.add(LeaderboardEntryDto.builder()
                    .rank(rank++)
                    .userId(userId)
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .profileImageUrl(user.getProfileImageUrl())
                    .correctCount((int) correctCount)
                    .totalQuestions(total)
                    .lastSubmissionTime(lastTime)
                    .build());
        }
        return entries;
    }
}