package com.robotest.repository;

import com.robotest.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByContestIdOrderByOrderIndexAsc(Long contestId);
    long countByContestId(Long contestId);
}