package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.ExamService;
import com.example.demo.repository.ExamRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/exam")
@CrossOrigin(origins = "*")
@Tag(name = "Exam Engine", description = "Endpoints for starting and submitting exams")
public class ExamController {

    @Autowired
    private ExamService service;

    @Autowired
    private ExamRepository examRepo;

    @GetMapping("/list")
    public java.util.List<Exam> list() {
        return examRepo.findAll();
    }

    @GetMapping("/{examId}/questions")
    @Operation(summary = "Get questions for an exam", description = "Retrieves a shuffled list of questions for the specified exam", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Successfully retrieved questions")
    public java.util.List<Question> getQuestions(@PathVariable int examId) {
        return service.getQuestionsForExam(examId);
    }

    /**
     * Starts a new exam session or resumes an existing IN_PROGRESS one.
     */
    @PostMapping("/start")
    @Operation(summary = "Start or resume an exam", description = "Creates a new exam attempt or returns an existing IN_PROGRESS one", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Exam attempt started/resumed")
    public ExamAttempt start(@RequestBody Map<String, Integer> req) {
        int userId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
        return service.startExam(userId, req.get("examId"));
    }

    /**
     * Finalizes the exam and calculates the score.
     */
    @PostMapping("/submit")
    @Operation(summary = "Submit an exam", description = "Completes an exam attempt and calculates the final score", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Exam submitted and result generated")
    public Result submit(@RequestBody Map<String, Integer> req) {
        return service.submitExam(req.get("attemptId"));
    }
}