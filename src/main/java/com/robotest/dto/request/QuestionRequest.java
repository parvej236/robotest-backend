package com.robotest.dto.request;

import com.robotest.enums.QuestionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionRequest {

    @NotBlank(message = "Description is required")
    private String      description;

    @NotNull(message = "Question type is required")
    private QuestionType type;

    @Min(value = 0, message = "Time limit cannot be negative")
    private Integer timeLimit; // Seconds

    private Double  correctAnswer;    // weight in grams for NUMERIC_MCQ
    private Double  errorPercentage;  // tolerance %
    private String  customAnswerKey;  // for CUSTOM type

    private Integer orderIndex;

    @NotNull(message = "Points are required")
    @Min(value = 1, message = "Points must be at least 1")
    private Integer points;
}