package com.robotest.controller;

import com.robotest.entity.Rulebook;
import com.robotest.repository.RulebookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/rulebook")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RulebookController {

    private final RulebookRepository rulebookRepository;

    /**
     * GET /api/rulebook
     * Publicly accessible endpoint to retrieve the contest rules[cite: 14].
     */
    @GetMapping
    public ResponseEntity<Rulebook> getRulebook() {
        // Return the existing rulebook or a default protocol structure if empty
        return rulebookRepository.findById(1L)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(Rulebook.createDefault()));
    }

    /**
     * PUT /api/rulebook
     * Restricted to ADMIN users. Updates the official protocol sections[cite: 115].
     */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Rulebook> updateRulebook(@RequestBody Map<String, Object> payload) {
        return rulebookRepository.findById(1L)
                .map(rulebook -> {
                    // Update sections (e.g., speed_modeling, mechanism_design) [cite: 26, 48]
                    if (payload.containsKey("sections")) {
                        rulebook.setSections((Map<String, Object>) payload.get("sections"));
                    }

                    // Update metadata (e.g., version, weightage) [cite: 94, 95]
                    if (payload.containsKey("metadata")) {
                        rulebook.setMetadata((Map<String, Object>) payload.get("metadata"));
                    }

                    rulebook.setUpdatedAt(OffsetDateTime.now());
                    return ResponseEntity.ok(rulebookRepository.save(rulebook));
                })
                .orElseGet(() -> {
                    // Initialize rulebook entry if it does not exist
                    Rulebook newRulebook = new Rulebook();
                    newRulebook.setId(1L);
                    newRulebook.setSections((Map<String, Object>) payload.get("sections"));
                    newRulebook.setMetadata((Map<String, Object>) payload.get("metadata"));
                    newRulebook.setUpdatedAt(OffsetDateTime.now());
                    return ResponseEntity.ok(rulebookRepository.save(newRulebook));
                });
    }
}