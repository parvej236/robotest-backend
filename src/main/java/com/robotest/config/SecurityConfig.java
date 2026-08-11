package com.robotest.config;

import com.robotest.security.JwtAuthenticationFilter;
import com.robotest.security.OAuth2AuthenticationSuccessHandler;
import com.robotest.security.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl  userDetailsServiceImpl;
    private final OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;
    private final PasswordEncoder passwordEncoder;

    /**
     * Explicit constructor (NOT @RequiredArgsConstructor) so that
     * the @Bean userDetailsService() below does NOT get injected
     * as a second constructor parameter by Lombok — avoiding conflicts.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsServiceImpl  userDetailsServiceImpl,
                          OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler,
                          PasswordEncoder passwordEncoder) {
        this.jwtAuthFilter           = jwtAuthFilter;
        this.userDetailsServiceImpl  = userDetailsServiceImpl;
        this.oAuth2SuccessHandler    = oAuth2SuccessHandler;
        this.passwordEncoder         = passwordEncoder;
    }

    /**
     * Expose the concrete impl as the UserDetailsService interface bean.
     * DaoAuthenticationProvider.setUserDetailsService() requires the
     * interface type — calling userDetailsService() below satisfies that.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsServiceImpl;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // ── Public endpoints ──
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/verify-email").permitAll()
                .requestMatchers("/api/auth/resend-verification").permitAll()
                .requestMatchers("/api/auth/check-username").permitAll()
                .requestMatchers("/api/auth/check-email").permitAll()
                .requestMatchers("/api/auth/forgot-password").permitAll()
                .requestMatchers("/api/auth/reset-password").permitAll()
                .requestMatchers("/api/auth/refresh-token").permitAll()
                .requestMatchers("/api/auth/validate-token").permitAll()
                .requestMatchers("/login/oauth2/**", "/oauth2/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/contests/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/leaderboard/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/rulebook").permitAll()
                    .requestMatchers("/api/submissions/**").permitAll()
                    .requestMatchers("/uploads/**").permitAll()
                    // ── Static frontend assets & SPA routes ──
                    .requestMatchers("/", "/index.html").permitAll()
                    .requestMatchers("/assets/**", "/favicon.ico", "/favicon.svg").permitAll()
                    .requestMatchers(
                        "/login", "/register", "/verify-email", "/reset-password",
                        "/forgot-password", "/about", "/rules", "/contests",
                        "/leaderboard"
                    ).permitAll()
                    .requestMatchers("/api/users/me").authenticated()
                    .requestMatchers("/api/users/me/avatar").authenticated()
                    .requestMatchers("/api/users/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/rulebook").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2SuccessHandler)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        // Calls the @Bean above — returns UserDetailsService interface; no cast error
        p.setUserDetailsService(userDetailsService());
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg)
            throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of(
                "*"
        ));
        cfg.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }
}
