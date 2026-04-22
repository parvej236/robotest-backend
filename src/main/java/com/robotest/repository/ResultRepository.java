package com.robotest.repository;

import com.robotest.entity.Result;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByUserIdAndContestId(Long userId, Long contestId);
    List<Result> findByContestIdOrderByRankAsc(Long contestId);

    @EntityGraph(attributePaths = {"user", "contest"})
    List<Result> findByUserId(Long userId);

    @jakarta.transaction.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Result r WHERE r.contest.id = :contestId")
    void deleteByContestId(@org.springframework.data.repository.query.Param("contestId") Long contestId);
}