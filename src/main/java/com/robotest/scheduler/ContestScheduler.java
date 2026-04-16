package com.robotest.scheduler;

import com.robotest.entity.Contest;
import com.robotest.enums.ContestStatus;
import com.robotest.repository.ContestRepository;
import com.robotest.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContestScheduler {

    private final ContestRepository  contestRepository;
    private final LeaderboardService leaderboardService;

    /**
     * Runs every 60 seconds.
     *
     * Correct state machine:
     *
     *   UPCOMING
     *     → REGISTRATION_OPEN   when registrationStart <= now
     *     → RUNNING             when contestStart <= now  (skips if reg window passed)
     *
     *   REGISTRATION_OPEN
     *     → RUNNING             when contestStart <= now
     *     (registration window end is informational only — does NOT change status)
     *
     *   RUNNING
     *     → FINISHED            when contestEnd < now
     *
     * FIX: removed the "close registration → UPCOMING" step that was causing
     * contests to flip back and forth every minute. A contest should never
     * go backwards in status.
     */
    @Scheduled(fixedRate = 5_000)
    public void updateContestStatuses() {
        LocalDateTime now = LocalDateTime.now();

        // ── UPCOMING → REGISTRATION_OPEN ──────────────────────
        // Only open if contestStart is still in the future
        // (avoids opening registration for a contest that should already be running)
        List<Contest> toOpenReg = contestRepository
                .findByStatusAndRegistrationStartBeforeAndContestStartAfter(
                        ContestStatus.UPCOMING, now, now);
        toOpenReg.forEach(c -> {
            c.setStatus(ContestStatus.REGISTRATION_OPEN);
            contestRepository.save(c);
            log.info("[Scheduler] Registration opened: {}", c.getName());
        });

        // ── UPCOMING or REGISTRATION_OPEN → RUNNING ────────────
        // Contest start time has been reached
        List<Contest> toStart = contestRepository
                .findByStatusInAndContestStartBefore(
                        List.of(ContestStatus.UPCOMING, ContestStatus.REGISTRATION_OPEN), now);
        toStart.forEach(c -> {
            c.setStatus(ContestStatus.RUNNING);
            contestRepository.save(c);
            log.info("[Scheduler] Contest started: {}", c.getName());
        });

        // ── RUNNING → FINISHED ─────────────────────────────────
        List<Contest> toFinish = contestRepository
                .findByStatusAndContestEndBefore(ContestStatus.RUNNING, now);
        toFinish.forEach(c -> {
            c.setStatus(ContestStatus.FINISHED);
            contestRepository.save(c);
            log.info("[Scheduler] Contest finished: {}", c.getName());
            try {
                leaderboardService.calculateAndSaveResults(c.getId());
            } catch (Exception e) {
                log.error("[Scheduler] Failed to calculate results for {}: {}",
                        c.getName(), e.getMessage());
            }
        });
    }
}