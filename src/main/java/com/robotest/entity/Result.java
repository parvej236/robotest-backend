package com.robotest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "results",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "contest_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    private Integer correctCount;
    private Integer totalQuestions;
    private Integer rank;
    private LocalDateTime lastSubmissionTime;

    @CreationTimestamp
    private LocalDateTime calculatedAt;
}