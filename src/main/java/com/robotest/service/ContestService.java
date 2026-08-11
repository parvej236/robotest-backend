package com.robotest.service;

import com.robotest.dto.request.ContestRequest;
import com.robotest.dto.response.ApiResponse;
import com.robotest.dto.response.ContestDto;
import com.robotest.entity.Contest;
import com.robotest.entity.Registration;
import com.robotest.entity.User;
import com.robotest.enums.ContestStatus;
import com.robotest.exception.AppException;
import com.robotest.repository.ContestRepository;
import com.robotest.repository.QuestionRepository;
import com.robotest.repository.RegistrationRepository;
import com.robotest.repository.ResultRepository;
import com.robotest.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestService {

    private final ContestRepository      contestRepository;
    private final RegistrationRepository registrationRepository;
    private final QuestionRepository     questionRepository;
    private final UserService            userService;
    private final EmailService           emailService;
    private final SubmissionRepository   submissionRepository;
    private final ResultRepository       resultRepository;

    // ── GET ALL ───────────────────────────────────────────────
    public ApiResponse<List<ContestDto>> getAllContests() {
        List<ContestDto> list = contestRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
        return ApiResponse.success("Contests fetched", list);
    }

    // ── GET ACTIVE (registration_open + running) ───────────────
    public ApiResponse<List<ContestDto>> getActiveContests() {
        List<ContestDto> list = contestRepository
                .findByStatusIn(List.of(ContestStatus.REGISTRATION_OPEN, ContestStatus.RUNNING))
                .stream().map(this::toDto).collect(Collectors.toList());
        return ApiResponse.success("Active contests fetched", list);
    }

    // ── GET LATEST 10 ─────────────────────────────────────────
    public ApiResponse<List<ContestDto>> getLatestContests() {
        List<ContestDto> list = contestRepository.findTop10ByOrderByCreatedAtDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
        return ApiResponse.success("Latest contests fetched", list);
    }

    // ── GET BY ID ─────────────────────────────────────────────
    public ApiResponse<ContestDto> getContestById(Long id) {
        return ApiResponse.success("Contest fetched", toDto(findById(id)));
    }

    // ── CREATE ────────────────────────────────────────────────
    @Transactional
    public ApiResponse<ContestDto> createContest(ContestRequest req) {
        validateDates(req);
        Contest contest = Contest.builder()
                .name(req.getName())
                .description(req.getDescription())
                .contestDate(req.getContestDate())
                .registrationStart(req.getRegistrationStart())
                .registrationEnd(req.getRegistrationEnd())
                .contestStart(req.getContestStart())
                .contestEnd(req.getContestEnd())
                .status(ContestStatus.UPCOMING)
                .build();
        Contest saved = contestRepository.save(contest);
        log.info("Contest created: {}", saved.getName());

//        List<User> users = userService.findAll();
//        users.forEach(user -> {
//            emailService.sendNewContestEmail(
//                    user.getEmail(),
//                    user.getFullName(),
//                    saved.getName(),
//                    saved.getId().toString()
//            );
//        });

        return ApiResponse.success("Contest created", toDto(saved));
    }

    // ── UPDATE ────────────────────────────────────────────────
    @Transactional
    public ApiResponse<ContestDto> updateContest(Long id, ContestRequest req) {
        Contest contest = findById(id);
        validateDates(req);
        contest.setName(req.getName());
        contest.setDescription(req.getDescription());
        contest.setContestDate(req.getContestDate());
        contest.setRegistrationStart(req.getRegistrationStart());
        contest.setRegistrationEnd(req.getRegistrationEnd());
        contest.setContestStart(req.getContestStart());
        contest.setContestEnd(req.getContestEnd());
        return ApiResponse.success("Contest updated", toDto(contestRepository.save(contest)));
    }

    // ── DELETE ────────────────────────────────────────────────
    @Transactional
    public ApiResponse<String> deleteContest(Long id) {
        submissionRepository.deleteByContestId(id);
        resultRepository.deleteByContestId(id);
        contestRepository.delete(findById(id));
        log.info("Contest deleted: {}", id);
        return ApiResponse.success("Contest deleted");
    }

    // ── REGISTER USER ─────────────────────────────────────────
    @Transactional
    public ApiResponse<String> registerForContest(Long contestId, String email) {
        User user = userService.findByEmail(email);
        Contest contest = findById(contestId);

        if (contest.getStatus() != ContestStatus.REGISTRATION_OPEN)
            throw AppException.badRequest("Registration is not open for this contest");
        if (registrationRepository.existsByUserIdAndContestId(user.getId(), contestId))
            throw AppException.conflict("You are already registered for this contest");

        registrationRepository.save(Registration.builder()
                .user(user).contest(contest).build());

        emailService.sendRegistrationSuccessEmail(user.getEmail(), user.getFullName(), contest.getName());
        return ApiResponse.success("Successfully registered for contest");
    }

    // ── CHECK IF REGISTERED ───────────────────────────────────
    public ApiResponse<Boolean> isRegistered(Long contestId, String email) {
        User user = userService.findByEmail(email);
        return ApiResponse.success("Registration status",
                registrationRepository.existsByUserIdAndContestId(user.getId(), contestId));
    }

    // ── MY CONTESTS ───────────────────────────────────────────
    public ApiResponse<List<ContestDto>> getUserContests(String email) {
        User user = userService.findByEmail(email);
        List<ContestDto> list = registrationRepository.findByUserId(user.getId())
                .stream().map(r -> toDto(r.getContest())).collect(Collectors.toList());
        return ApiResponse.success("My contests fetched", list);
    }

    // ── Helpers ───────────────────────────────────────────────
    public Contest findById(Long id) {
        return contestRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Contest not found: " + id));
    }

    public ContestDto toDto(Contest c) {
        return ContestDto.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .contestDate(c.getContestDate())
                .registrationStart(c.getRegistrationStart())
                .registrationEnd(c.getRegistrationEnd())
                .contestStart(c.getContestStart())
                .contestEnd(c.getContestEnd())
                .status(c.getStatus())
                .registrationCount(registrationRepository.countByContestId(c.getId()))
                .questionCount((int) questionRepository.countByContestId(c.getId()))
                .createdAt(c.getCreatedAt())
                .build();
    }

    private void validateDates(ContestRequest req) {
        if (req.getRegistrationEnd().isBefore(req.getRegistrationStart()))
            throw AppException.badRequest("Registration end must be after registration start");
        if (req.getContestEnd().isBefore(req.getContestStart()))
            throw AppException.badRequest("Contest end must be after contest start");
    }

    @Transactional
    public void sendContestAnnouncementEmail(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found with id: " + contestId));

        List<User> users = userService.findAll();
        users.forEach(user -> {
            emailService.sendNewContestEmail(
                    user.getEmail(),
                    user.getFullName(),
                    contest.getName(),
                    contest.getId().toString()
            );
        });

        log.info("Contest announcement emails sent for contest: {}", contest.getName());
    }
}