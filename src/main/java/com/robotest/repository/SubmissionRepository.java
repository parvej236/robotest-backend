package com.robotest.repository;

import com.robotest.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByUserIdAndContestId(Long userId, Long contestId);
    Optional<Submission> findByUserIdAndQuestionId(Long userId, Long questionId);
    long countByUserIdAndContestIdAndCorrect(Long userId, Long contestId, boolean correct);

    @Query("""
        SELECT s.user.id, COUNT(s) as correctCount, MAX(s.submittedAt) as lastTime
        FROM Submission s
        WHERE s.contest.id = :contestId AND s.correct = true
        GROUP BY s.user.id
        ORDER BY correctCount DESC, lastTime ASC
        """)
    List<Object[]> findLeaderboardData(Long contestId);

    boolean existsByContest_IdAndUser_Email(Long contestId, String email);
}