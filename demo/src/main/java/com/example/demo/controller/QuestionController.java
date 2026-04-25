package com.example.demo.controller;

import com.example.demo.model.Question;
import com.example.demo.service.ExamService;
import com.example.demo.repository.QuestionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
@Tag(name = "Questions", description = "Endpoints for retrieving exam questions")
public class QuestionController {

    @Autowired
    private ExamService examService;

    @Autowired
    private QuestionRepository questionRepo;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all questions", description = "Returns a list of all questions in the bank (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public List<Question> getAllQuestions() {
        return questionRepo.findAll();
    }
    @GetMapping("/exam/{examId}")
    @Operation(summary = "Get questions for an exam", description = "Returns a shuffled list of questions for a specific exam ID")
    public List<Question> getQuestionsByExam(@PathVariable int examId) {
        // Now returns shuffled questions via ExamService
        return examService.getQuestionsForExam(examId);
    }
}
