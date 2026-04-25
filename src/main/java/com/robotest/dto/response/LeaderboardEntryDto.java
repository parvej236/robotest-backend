package com.robotest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardEntryDto {
    private int rank;
    private Long userId;
    private String username;
    private String fullName;
    private String profileImageUrl;

    /**
     * Total score calculated after time penalties (unitary method)
     * and wrong attempt penalties (2% per error).
     */
    private double totalScore;

    private int totalQuestions;

    /**
     * The timestamp of the very last correct submission.
     * Used as a tie-breaker in ranking.
     */
    private LocalDateTime lastSubmissionTime;

    /**
     * Question-wise breakdown for the leaderboard grid.
     */
    private List<QuestionStatusDto> questionStatuses;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionStatusDto {
        private Long questionId;
        private boolean correct;

        /**
         * The specific points earned for this individual question.
         */
        private double score;

        private Integer wrongCount;
        private LocalDateTime submittedAt;

        // New fields
        private Long timeTakenSeconds;
        private Double timeLimits;
        private Double penalty;

        private Double timePenalty;
        private Double wrongPenalty;
    }
}