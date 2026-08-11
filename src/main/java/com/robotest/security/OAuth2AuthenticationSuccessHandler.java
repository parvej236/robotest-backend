package com.robotest.security;

import com.robotest.entity.Role;
import com.robotest.entity.User;
import com.robotest.enums.RoleName;
import com.robotest.repository.RoleRepository;
import com.robotest.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public OAuth2AuthenticationSuccessHandler(UserRepository userRepository,
                                             RoleRepository roleRepository,
                                             JwtService jwtService,
                                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        if (email == null) {
            response.sendRedirect(frontendUrl + "/login?error=oauth_no_email");
            return;
        }

        final String normalizedEmail = email.toLowerCase().trim();

        User user = userRepository.findByEmail(normalizedEmail)
                .map(u -> {
                    boolean modified = false;
                    if (!u.isEnabled() || !u.isEmailVerified()) {
                        u.setEnabled(true);
                        u.setEmailVerified(true);
                        modified = true;
                    }
                    if ((u.getProfileImageUrl() == null || u.getProfileImageUrl().isEmpty()) && picture != null) {
                        u.setProfileImageUrl(picture);
                        modified = true;
                    }
                    if (modified) {
                        return userRepository.save(u);
                    }
                    return u;
                })
                .orElseGet(() -> {
                    Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build()));

                    User newUser = new User();
                    newUser.setEmail(normalizedEmail);
                    newUser.setFullName(name != null ? name : normalizedEmail.split("@")[0]);
                    newUser.setProfileImageUrl(picture);
                    
                    // Ensure username is unique
                    String baseUsername = normalizedEmail.split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
                    if (baseUsername.isEmpty()) {
                        baseUsername = "user";
                    }
                    String username = baseUsername;
                    int count = 1;
                    while (userRepository.existsByUsername(username)) {
                        username = baseUsername + count;
                        count++;
                    }
                    newUser.setUsername(username);
                    
                    // Hashed random password for security schema compliance
                    newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    newUser.setEnabled(true);
                    newUser.setEmailVerified(true);
                    newUser.setRoles(new HashSet<>(Set.of(userRole)));
                    return userRepository.save(newUser);
                });

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority(r.getName().name()))
                        .collect(Collectors.toList()))
                .disabled(!user.isEnabled())
                .build();

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        String baseUrl = frontendUrl;
        if (baseUrl != null && baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        String targetUrl = UriComponentsBuilder.fromUriString(baseUrl + "/oauth2/success")
                .queryParam("token", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
