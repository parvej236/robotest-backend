package com.robotest.entity;

import com.robotest.enums.ContestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 5000)
    private String description;

    private LocalDateTime contestDate;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private LocalDateTime contestStart;
    private LocalDateTime contestEnd;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ContestStatus status = ContestStatus.UPCOMING;

    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Registration> registrations = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}