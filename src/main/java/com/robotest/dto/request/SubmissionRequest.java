package com.robotest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
public class SubmissionRequest {
    @NotBlank(message = "Answer cannot be empty or blank")
    private String answer;
    private Instant questionStartedAt;
}