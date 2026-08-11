package com.robotest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.Map;

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

    private Double totalScore;     // Sum of all question scores
    private Integer solvedCount;   // Count of correct answers
    private Integer rank;

    @CreationTimestamp
    private LocalDateTime calculatedAt;
}