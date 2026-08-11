package com.robotest.dto.response;

import com.robotest.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class QuestionDto {
    private Long         id;
    private Long         contestId;
    private String       description;
    private String       imageUrl;
    private String       videoUrl;
    private QuestionType type;
    private Integer      timeLimit; // Seconds
    private Double       correctAnswer;
    private Double       errorPercentage;
    private String       customAnswerKey;
    private Integer      orderIndex;
    private Integer      points;
}