package com.robotest.repository;

import com.robotest.entity.Contest;
import com.robotest.enums.ContestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {

    // ── Used by ContestService ────────────────────────────────
    List<Contest> findByStatusIn(List<ContestStatus> statuses);

    List<Contest> findTop10ByOrderByCreatedAtDesc();

    // ── Scheduler: UPCOMING → REGISTRATION_OPEN ──────────────
    // Only open registration if contest hasn't started yet
    List<Contest> findByStatusAndRegistrationStartBeforeAndContestStartAfter(
            ContestStatus status,
            LocalDateTime now1,     // registrationStart <= now
            LocalDateTime now2);    // contestStart > now

    // ── Scheduler: UPCOMING or REGISTRATION_OPEN → RUNNING ───
    // Contest start time reached — also handles contests that skipped reg window
    List<Contest> findByStatusInAndContestStartBefore(
            List<ContestStatus> statuses,
            LocalDateTime now);

    // ── Scheduler: RUNNING → FINISHED ────────────────────────
    List<Contest> findByStatusAndContestEndBefore(
            ContestStatus status,
            LocalDateTime now);
}