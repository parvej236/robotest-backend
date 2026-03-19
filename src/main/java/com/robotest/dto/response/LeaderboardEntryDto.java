package com.robotest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class LeaderboardEntryDto {
    private int           rank;
    private Long          userId;
    private String        username;
    private String        fullName;
    private String        profileImageUrl;
    private int           correctCount;
    private int           totalQuestions;
    private LocalDateTime lastSubmissionTime;
}