package com.robotest.repository;

import com.robotest.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByUserIdAndContestId(Long userId, Long contestId);
    List<Result> findByContestIdOrderByRankAsc(Long contestId);
    List<Result> findByUserId(Long userId);
}