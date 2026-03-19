package com.robotest.controller;

import com.robotest.dto.request.QuestionRequest;
import com.robotest.dto.response.ApiResponse;
import com.robotest.dto.response.QuestionDto;
import com.robotest.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    // ── Admin: get all questions for a contest ─────────────────
    @GetMapping("/contest/{contestId}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<QuestionDto>>> getForAdmin(
            @PathVariable Long contestId) {
        return ResponseEntity.ok(questionService.getQuestionsForAdmin(contestId));
    }

    // ── Admin: add question with optional image/video ──────────
    @PostMapping(value = "/contest/{contestId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<QuestionDto>> add(
            @PathVariable Long contestId,
            @RequestPart("question") QuestionRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "video", required = false) MultipartFile video) {
        return ResponseEntity.ok(
                questionService.addQuestion(contestId, request, image, video));
    }

    // ── Admin: update question ────────────────────────────────
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<QuestionDto>> update(
            @PathVariable Long id,
            @RequestPart("question") QuestionRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "video", required = false) MultipartFile video) {
        return ResponseEntity.ok(
                questionService.updateQuestion(id, request, image, video));
    }

    // ── Admin: delete question ────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.deleteQuestion(id));
    }
}