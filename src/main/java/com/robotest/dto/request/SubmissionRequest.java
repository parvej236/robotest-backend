package com.robotest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmissionRequest {
    @NotBlank(message = "Answer cannot be empty or blank")
    private String answer;
}