package com.robotest.config;

import com.robotest.entity.Role;
import com.robotest.entity.User;
import com.robotest.enums.RoleName;
import com.robotest.repository.RoleRepository;
import com.robotest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository  roleRepository;
    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ── Seed roles ────────────────────────────────────────
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build());
            roleRepository.save(Role.builder().name(RoleName.ROLE_ADMIN).build());
            roleRepository.save(Role.builder().name(RoleName.ROLE_JUDGE).build());
            log.info("Roles seeded: ROLE_USER, ROLE_ADMIN");
        }

        // ── Seed admin account ────────────────────────────────
        if (!userRepository.existsByEmail("rmeducadcon@gmail.com")) {
            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow();
            Role userRole  = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow();

            User admin = User.builder()
                    .fullName("System Administrator")
                    .username("admin")
                    .email("rmeducadcon@gmail.com")
                    .password(passwordEncoder.encode("Admin@1234"))
                    .enabled(true)
                    .emailVerified(true)
                    .roles(Set.of(adminRole, userRole))
                    .build();

            userRepository.save(admin);
            log.info("Admin created ─ email: rmeducadcon@gmail.com  password: Admin@1234");
        }
    }
}
