package com.robotest.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ResultResponseDto {
    private Long id;
    private String contestName;
    private Long contestId;
    private Double totalScore;
    private Integer solvedCount;
    private Integer rank;
    private Map<Long, Double> questionScores;
    private LocalDateTime calculatedAt;
}