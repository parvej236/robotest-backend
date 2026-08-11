package com.robotest.entity;

import com.robotest.enums.QuestionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @Column(length = 5000)
    private String description;

    private String imageUrl;
    private String videoUrl;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    private Integer timeLimit; // Store as total seconds

    // For NUMERIC_MCQ: correct weight in grams
    private Double correctAnswer;

    // Tolerance percentage (e.g. 5.0 means ±5%)
    private Double errorPercentage;

    // For CUSTOM: exact answer string (case-insensitive match)
    @Column(length = 1000)
    private String customAnswerKey;

    private Integer orderIndex;
    private Integer points;
}