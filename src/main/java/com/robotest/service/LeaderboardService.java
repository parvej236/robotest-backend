package com.robotest.service;

import com.robotest.dto.response.ApiResponse;
import com.robotest.dto.response.LeaderboardEntryDto;
import com.robotest.dto.response.ResultResponseDto;
import com.robotest.entity.*;
import com.robotest.exception.AppException;
import com.robotest.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardService {

    private final SubmissionRepository submissionRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ResultRepository resultRepository;
    private final ContestService contestService;
    private final ContestRepository contestRepository;

    public ApiResponse<List<LeaderboardEntryDto>> getLeaderboard(Long contestId) {
        contestService.findById(contestId); // verify exists
        return ApiResponse.success("Leaderboard fetched", buildLeaderboard(contestId));
    }

    private List<LeaderboardEntryDto> buildLeaderboard(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> AppException.notFound("Contest not found"));

        List<Question> questions = questionRepository.findByContestIdOrderByOrderIndexAsc(contestId);
        List<Submission> allSubmissions = submissionRepository.findByContestId(contestId);

        // Group by User
        Map<Long, List<Submission>> userSubmissionsMap = allSubmissions.stream()
                .collect(Collectors.groupingBy(s -> s.getUser().getId()));

        List<LeaderboardEntryDto> entries = new ArrayList<>();

        for (Map.Entry<Long, List<Submission>> userEntry : userSubmissionsMap.entrySet()) {
            Long userId = userEntry.getKey();
            List<Submission> userSubs = userEntry.getValue();

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) continue;

            double totalScore = 0;
            List<LeaderboardEntryDto.QuestionStatusDto> qStatuses = new ArrayList<>();

            for (Question q : questions) {
                // Get the correct submission for this specific question
                Submission correctSub = userSubs.stream()
                        .filter(s -> s.getQuestion().getId().equals(q.getId()) && s.isCorrect())
                        .findFirst()
                        .orElse(null);

                if (correctSub != null) {
                    double basePoints = q.getPoints() != null ? q.getPoints() : 0.0;
                    Instant startTime = correctSub.getQuestionStartedAt() != null
                            ? correctSub.getQuestionStartedAt()
                            : contest.getContestStart().toInstant(ZoneOffset.UTC);
                    long secondsUsed = Duration.between(startTime, correctSub.getSubmittedAt()).getSeconds();
                    secondsUsed = Math.max(0, secondsUsed);

                    double timeLimit = q.getTimeLimit() != null ? q.getTimeLimit() : 3600.0;
                    double timePenalty = (basePoints * 0.5) * (Math.min(secondsUsed, timeLimit) / timeLimit);
                    double wrongPenalty = basePoints * 0.02 * (correctSub.getWrongCount() != null ? correctSub.getWrongCount() : 0);
                    double totalPenalty = timePenalty + wrongPenalty;

                    double qScore = Math.max(0.0, basePoints - totalPenalty);
                    totalScore += qScore;

                    qStatuses.add(LeaderboardEntryDto.QuestionStatusDto.builder()
                            .questionId(q.getId())
                            .correct(true)
                            .score(qScore)
                            .wrongCount(correctSub.getWrongCount())
                            .submittedAt(correctSub.getSubmittedAt())
                            .timeTakenSeconds(secondsUsed)
                            .timeLimits(timeLimit)
                            .penalty(totalPenalty)
                            .timePenalty(totalPenalty)
                            .wrongPenalty(wrongPenalty)
                            .build());
                } else {
                    // Try to find if there was a wrong attempt to show status in UI
                    Submission wrongSub = userSubs.stream()
                            .filter(s -> s.getQuestion().getId().equals(q.getId()))
                            .findFirst()
                            .orElse(null);

                    if (wrongSub != null) {
                        double basePoints = q.getPoints() != null ? q.getPoints() : 0.0;
                        double wrongPenalty = basePoints * 0.02 * (wrongSub.getWrongCount() != null ? wrongSub.getWrongCount() : 0);


                        double qScore = - wrongPenalty;
                        totalScore += qScore;

                        qStatuses.add(LeaderboardEntryDto.QuestionStatusDto.builder()
                                .questionId(q.getId())
                                .correct(false)
                                .score(qScore)
                                .wrongCount(wrongSub.getWrongCount())
                                .wrongPenalty(wrongPenalty)
                                .submittedAt(wrongSub.getSubmittedAt())
                                .build());
                    } else {
                        qStatuses.add(null); // Not even attempted
                    }
                }
            }

            entries.add(LeaderboardEntryDto.builder()
                    .userId(userId)
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .profileImageUrl(user.getProfileImageUrl())
                    .totalScore(totalScore)
                    .totalQuestions(questions.size())
                    .questionStatuses(qStatuses)
                    .lastSubmissionTime(getLastSubmissionTime(userSubs))
                    .build());
        }

        // Sort: High score first. If scores equal, earlier lastSubmissionTime wins.
        entries.sort(Comparator.comparingDouble(LeaderboardEntryDto::getTotalScore).reversed()
                .thenComparing(LeaderboardEntryDto::getLastSubmissionTime,
                        Comparator.nullsLast(Comparator.naturalOrder())));

        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }

        return entries;
    }

    private double calculateQuestionScore(Question q, Submission sub, Contest contest) {
        double basePoints = q.getPoints() != null ? q.getPoints() : 0.0;
        if (basePoints == 0) return 0.0;

        // 1. Time Penalty (Unitary Method)
        Instant startTime = sub.getQuestionStartedAt() != null
                ? sub.getQuestionStartedAt()
                : contest.getContestStart().toInstant(ZoneOffset.UTC);
        long secondsUsed = Duration.between(startTime, sub.getSubmittedAt()).getSeconds();
        secondsUsed = Math.max(0, secondsUsed); // Ensure we don't get negative time
        
        double timeLimit = q.getTimeLimit() != null ? q.getTimeLimit() : 3600.0; // default 1hr if null

        // Penalty = (Points * 0.5) * (Used / Limit). Cap used time to timeLimit.
        double timePenalty = (basePoints * 0.5) * (Math.min(secondsUsed, timeLimit) / timeLimit);

        // 2. Wrong Attempt Penalty (2% per wrong count)
        double wrongPenalty = basePoints * 0.02 * (sub.getWrongCount() != null ? sub.getWrongCount() : 0);

        double finalScore = basePoints - timePenalty - wrongPenalty;
        return Math.max(0.0, finalScore);
    }

    private Instant getLastSubmissionTime(List<Submission> userSubs) {
        return userSubs.stream()
                .filter(Submission::isCorrect)
                .map(Submission::getSubmittedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    @Transactional
    public void calculateAndSaveResults(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> AppException.notFound("Contest not found"));

        List<LeaderboardEntryDto> lb = buildLeaderboard(contestId);

        for (LeaderboardEntryDto entry : lb) {
            User user = userRepository.findById(entry.getUserId()).orElse(null);
            if (user == null) continue;

            Result result = resultRepository.findByUserIdAndContestId(user.getId(), contestId)
                    .orElse(Result.builder()
                            .user(user)
                            .contest(contest)
                            .totalScore(entry.getTotalScore())
                            .build());

            // Map DTO question statuses to Entity question scores
            Map<Long, Double> scoresMap = new HashMap<>();
            int solvedCount = 0;

            for (LeaderboardEntryDto.QuestionStatusDto qStatus : entry.getQuestionStatuses()) {
                if (qStatus != null) {
                    scoresMap.put(qStatus.getQuestionId(), qStatus.getScore());
                    if (qStatus.isCorrect()) {
                        solvedCount++;
                    }
                }
            }

            result.setTotalScore(entry.getTotalScore());
            result.setSolvedCount(solvedCount);
            result.setRank(entry.getRank());

            resultRepository.save(result);
        }
        log.info("Detailed results saved for contest {}", contestId);
    }

    @Transactional(readOnly = true)
    public List<ResultResponseDto> getMyResults(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.notFound("User not found"));

        List<Result> results = resultRepository.findByUserId(user.getId());

        return results.stream().map(result -> ResultResponseDto.builder()
                .id(result.getId())
                .contestId(result.getContest().getId())
                .contestName(result.getContest().getName())
                .totalScore(result.getTotalScore())
                .solvedCount(result.getSolvedCount())
                .rank(result.getRank())
                .calculatedAt(result.getCalculatedAt())
                .build()
        ).collect(Collectors.toList());
    }
}