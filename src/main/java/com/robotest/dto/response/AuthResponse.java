package com.robotest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long   expiresIn;
    private UserInfo user;

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class UserInfo {
        private Long         id;
        private String       fullName;
        private String       username;
        private String       email;
        private List<String> roles;
        private boolean      emailVerified;
        private String profileImageUrl;
    }
}
