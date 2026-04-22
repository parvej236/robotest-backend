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

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionService {

    private final SubmissionRepository   submissionRepository;
    private final QuestionRepository     questionRepository;
    private final RegistrationRepository registrationRepository;
    private final ContestService         contestService;
    private final UserService             userService;

    @Transactional
    public ApiResponse<String> submitSingleAnswer(Long contestId, String email, Long questionId, String answer, LocalDateTime questionStartedAt) {
        User user = userService.findByEmail(email);
        Contest contest = contestService.findById(contestId);

        // 1. Basic Validation
        if (contest.getStatus() != ContestStatus.RUNNING)
            throw AppException.badRequest("Contest is not currently running");

        if (!registrationRepository.existsByUserIdAndContestId(user.getId(), contestId))
            throw AppException.forbidden("You are not registered for this contest");

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> AppException.notFound("Question not found"));

        // 2. Check if already answered correctly
        Submission existing = submissionRepository.findByUserIdAndQuestionId(user.getId(), questionId).orElse(null);
        if (existing != null && existing.isCorrect()) {
            return ApiResponse.success("Question already solved correctly");
        }

        // 3. Evaluate the answer
        boolean correct = evaluate(question, answer);

        if (existing == null) {
            // New submission record
            existing = Submission.builder()
                    .user(user)
                    .contest(contest)
                    .question(question)
                    .submittedAnswer(answer)
                    .correct(correct)
                    .wrongCount(correct ? 0 : 1)
                    .questionStartedAt(questionStartedAt)
                    .build();
        } else {
            // Update existing record
            existing.setSubmittedAnswer(answer);
            existing.setCorrect(correct);
            if (!correct) {
                // Increment wrong count if the answer is incorrect
                existing.setWrongCount((existing.getWrongCount() == null ? 0 : existing.getWrongCount()) + 1);
            }
        }

        submissionRepository.save(existing);

        if (correct) {
            return ApiResponse.success("Correct! Moving to next question...");
        } else {
            // Throw an exception or return error to trigger the "Try Again" message in frontend
            return ApiResponse.error("Wrong answer, the value is not within the tolerance range, please try again.");
        }
    }

    private boolean evaluate(Question q, String answer) {
        if (answer == null || answer.isBlank()) return false;
        if (q.getType() == QuestionType.NUMERIC_MCQ) {
            try {
                double submitted = Double.parseDouble(answer.trim());
                double correct   = q.getCorrectAnswer();
                double tolerance = q.getErrorPercentage() != null
                        ? Math.abs(correct) * q.getErrorPercentage() / 100.0 : 0;
                return Math.abs(submitted - correct) <= tolerance;
            } catch (NumberFormatException e) { return false; }
        }
        return q.getCustomAnswerKey() != null && q.getCustomAnswerKey().equalsIgnoreCase(answer.trim());
    }

    public boolean hasUserSubmitted(Long contestId, String email) {
        return submissionRepository.existsByContest_IdAndUser_Email(contestId, email);
    }
}