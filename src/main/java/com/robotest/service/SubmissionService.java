package com.robotest.service;

import com.robotest.dto.request.SubmissionRequest;
import com.robotest.dto.response.ApiResponse;
import com.robotest.entity.*;
import com.robotest.enums.ContestStatus;
import com.robotest.enums.QuestionType;
import com.robotest.exception.AppException;
import com.robotest.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionService {

    private final SubmissionRepository   submissionRepository;
    private final QuestionRepository     questionRepository;
    private final RegistrationRepository registrationRepository;
    private final ContestService         contestService;
    private final UserService            userService;

    @Transactional
    public ApiResponse<String> submitAnswers(Long contestId, String email,
                                             SubmissionRequest req) {
        User user = userService.findByEmail(email);
        Contest contest = contestService.findById(contestId);

        if (contest.getStatus() != ContestStatus.RUNNING)
            throw AppException.badRequest("Contest is not currently running");
        if (!registrationRepository.existsByUserIdAndContestId(user.getId(), contestId))
            throw AppException.forbidden("You are not registered for this contest");

        for (Map.Entry<Long, String> entry : req.getAnswers().entrySet()) {
            Long   qId    = entry.getKey();
            String answer = entry.getValue();

            Question question = questionRepository.findById(qId)
                    .orElseThrow(() -> AppException.notFound("Question not found: " + qId));

            boolean correct = evaluate(question, answer);

            // Upsert — allow re-submission, overwrite previous answer
            submissionRepository.findByUserIdAndQuestionId(user.getId(), qId)
                    .ifPresentOrElse(existing -> {
                        existing.setSubmittedAnswer(answer);
                        existing.setCorrect(correct);
                        submissionRepository.save(existing);
                    }, () -> submissionRepository.save(Submission.builder()
                            .user(user)
                            .contest(contest)
                            .question(question)
                            .submittedAnswer(answer)
                            .correct(correct)
                            .build()));
        }

        log.info("Answers submitted by {} for contest {}", email, contestId);
        return ApiResponse.success("Answers submitted successfully");
    }

    // ── Answer evaluation ─────────────────────────────────────
    private boolean evaluate(Question q, String answer) {
        if (answer == null || answer.isBlank()) return false;

        if (q.getType() == QuestionType.NUMERIC_MCQ) {
            try {
                double submitted = Double.parseDouble(answer.trim());
                double correct   = q.getCorrectAnswer();
                double tolerance = q.getErrorPercentage() != null
                        ? Math.abs(correct) * q.getErrorPercentage() / 100.0 : 0;
                return Math.abs(submitted - correct) <= tolerance;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        // CUSTOM — case-insensitive exact match
        return q.getCustomAnswerKey() != null
                && q.getCustomAnswerKey().equalsIgnoreCase(answer.trim());
    }
}