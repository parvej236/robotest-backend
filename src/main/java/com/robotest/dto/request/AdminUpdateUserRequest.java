package com.robotest.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AdminUpdateUserRequest {
    private String       fullName;
    private String       gender;
    private String       registrationNumber;
    private String       rollNumber;
    private String       university;
    private String       hobby;
    private String       bio;
    private Boolean      enabled;
    private Boolean      emailVerified;
    private List<String> roles;   // e.g. ["ROLE_USER", "ROLE_ADMIN"]
}