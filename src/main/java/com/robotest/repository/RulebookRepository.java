package com.robotest.repository;

import com.robotest.entity.Rulebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RulebookRepository extends JpaRepository<Rulebook, Long> {
}
