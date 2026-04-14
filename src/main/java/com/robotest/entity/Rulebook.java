package com.robotest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "rulebook")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rulebook {

    @Id
    private Long id;

    /**
     * The 'sections' column in PostgreSQL is JSONB.
     * @JdbcTypeCode(SqlTypes.JSON) tells Hibernate 6 to use its
     * built-in JSON support for mapping to a Java Map.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> sections;

    /**
     * Stores additional metadata like versioning and organization details.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> metadata;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    /**
     * Helper method to initialize a default rulebook structure
     * if the database is empty.
     */
    public static Rulebook createDefault() {
        Rulebook defaultBook = new Rulebook();
        defaultBook.setId(1L);
        defaultBook.setSections(Map.of(
                "overview", "Initial protocol pending sync...",
                "eligibility", "RMEDU students only.",
                "fair_play", "Original work required."
        ));
        defaultBook.setMetadata(Map.of(
                "version", "1.0",
                "organization", "RMEDU Robotics Society"
        ));
        return defaultBook;
    }
}