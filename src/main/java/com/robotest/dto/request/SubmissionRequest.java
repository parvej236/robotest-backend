package com.robotest.dto.request;

import lombok.Data;
import java.util.Map;

@Data
public class SubmissionRequest {
    // Map of questionId → submitted answer string
    private Map<Long, String> answers;
}