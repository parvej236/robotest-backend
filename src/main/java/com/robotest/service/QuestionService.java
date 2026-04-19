package com.robotest.service;

import com.robotest.dto.request.QuestionRequest;
import com.robotest.dto.response.ApiResponse;
import com.robotest.dto.response.QuestionDto;
import com.robotest.entity.Contest;
import com.robotest.entity.Question;
import com.robotest.enums.ContestStatus;
import com.robotest.exception.AppException;
import com.robotest.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ContestService     contestService;
    private final FileStorageService fileStorageService;

    // ── GET FOR USER (only when contest is running or finished) ──
    public ApiResponse<List<QuestionDto>> getQuestionsForUser(Long contestId) {
        Contest c = contestService.findById(contestId);
        if (c.getStatus() == ContestStatus.UPCOMING
                || c.getStatus() == ContestStatus.REGISTRATION_OPEN) {
            throw AppException.forbidden("Questions are not available until the contest starts");
        }
        return ApiResponse.success("Questions fetched", toList(contestId));
    }

    // ── GET FOR ADMIN (always) ─────────────────────────────────
    public ApiResponse<List<QuestionDto>> getQuestionsForAdmin(Long contestId) {
        contestService.findById(contestId); // verify contest exists
        return ApiResponse.success("Questions fetched", toList(contestId));
    }

    // ── ADD QUESTION ──────────────────────────────────────────
    @Transactional
    public ApiResponse<QuestionDto> addQuestion(Long contestId,
                                                QuestionRequest req,
                                                MultipartFile image,
                                                MultipartFile video) {
        Contest contest = contestService.findById(contestId);

        Question q = Question.builder()
                .contest(contest)
                .description(req.getDescription())
                .type(req.getType())
                .timeLimit(req.getTimeLimit())
                .correctAnswer(req.getCorrectAnswer())
                .errorPercentage(req.getErrorPercentage())
                .customAnswerKey(req.getCustomAnswerKey())
                .orderIndex(req.getOrderIndex())
                .points(req.getPoints())
                .build();

        if (image != null && !image.isEmpty())
            q.setImageUrl(fileStorageService.storeFile(image, "questions/images"));
        if (video != null && !video.isEmpty())
            q.setVideoUrl(fileStorageService.storeFile(video, "questions/videos"));

        return ApiResponse.success("Question added", toDto(questionRepository.save(q)));
    }

    // ── UPDATE QUESTION ───────────────────────────────────────
    @Transactional
    public ApiResponse<QuestionDto> updateQuestion(Long questionId,
                                                   QuestionRequest req,
                                                   MultipartFile image,
                                                   MultipartFile video) {
        Question q = findById(questionId);

        q.setDescription(req.getDescription());
        q.setType(req.getType());
        q.setTimeLimit(req.getTimeLimit());
        q.setCorrectAnswer(req.getCorrectAnswer());
        q.setErrorPercentage(req.getErrorPercentage());
        q.setCustomAnswerKey(req.getCustomAnswerKey());
        q.setOrderIndex(req.getOrderIndex());
        q.setPoints(req.getPoints());

        if (image != null && !image.isEmpty()) {
            if (q.getImageUrl() != null) fileStorageService.delete(q.getImageUrl());
            q.setImageUrl(fileStorageService.storeFile(image, "questions/images"));
        }
        if (video != null && !video.isEmpty()) {
            if (q.getVideoUrl() != null) fileStorageService.delete(q.getVideoUrl());
            q.setVideoUrl(fileStorageService.storeFile(video, "questions/videos"));
        }

        return ApiResponse.success("Question updated", toDto(questionRepository.save(q)));
    }

    // ── DELETE QUESTION ───────────────────────────────────────
    @Transactional
    public ApiResponse<String> deleteQuestion(Long questionId) {
        Question q = findById(questionId);
        if (q.getImageUrl() != null) fileStorageService.delete(q.getImageUrl());
        if (q.getVideoUrl() != null) fileStorageService.delete(q.getVideoUrl());
        questionRepository.delete(q);
        return ApiResponse.success("Question deleted");
    }

    // ── Helpers ───────────────────────────────────────────────
    public Question findById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Question not found: " + id));
    }

    private List<QuestionDto> toList(Long contestId) {
        return questionRepository.findByContestIdOrderByOrderIndexAsc(contestId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public QuestionDto toDto(Question q) {
        return QuestionDto.builder()
                .id(q.getId())
                .contestId(q.getContest().getId())
                .description(q.getDescription())
                .imageUrl(q.getImageUrl())
                .videoUrl(q.getVideoUrl())
                .type(q.getType())
                .timeLimit(q.getTimeLimit())
                .correctAnswer(q.getCorrectAnswer())
                .errorPercentage(q.getErrorPercentage())
                .customAnswerKey(q.getCustomAnswerKey())
                .orderIndex(q.getOrderIndex())
                .points(q.getPoints())
                .build();
    }
}