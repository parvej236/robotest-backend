package com.robotest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContestRequest {

    @NotBlank(message = "Contest name is required")
    private String name;

    private String description;

    @NotNull(message = "Contest date is required")
    private LocalDateTime contestDate;

    @NotNull(message = "Registration start is required")
    private LocalDateTime registrationStart;

    @NotNull(message = "Registration end is required")
    private LocalDateTime registrationEnd;

    @NotNull(message = "Contest start is required")
    private LocalDateTime contestStart;

    @NotNull(message = "Contest end is required")
    private LocalDateTime contestEnd;
}