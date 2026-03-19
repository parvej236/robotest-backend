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
public class UserProfileResponse {
    private Long          id;
    private String        fullName;
    private String        username;
    private String        email;
    private String        profileImageUrl;
    private String        gender;
    private String        registrationNumber;
    private String        rollNumber;
    private String        university;
    private String        hobby;
    private String        bio;
    private List<String>  roles;
    private boolean       enabled;
    private boolean       emailVerified;
    private LocalDateTime createdAt;
}