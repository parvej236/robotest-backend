package com.robotest.dto.response;

import com.robotest.enums.ContestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ContestDto {
    private Long          id;
    private String        name;
    private String        description;
    private LocalDateTime contestDate;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private LocalDateTime contestStart;
    private LocalDateTime contestEnd;
    private ContestStatus status;
    private long          registrationCount;
    private int           questionCount;
    private LocalDateTime createdAt;
}