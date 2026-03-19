package com.robotest.repository;

import com.robotest.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    boolean existsByUserIdAndContestId(Long userId, Long contestId);
    Optional<Registration> findByUserIdAndContestId(Long userId, Long contestId);
    List<Registration> findByUserId(Long userId);
    List<Registration> findByContestId(Long contestId);
    long countByContestId(Long contestId);
}