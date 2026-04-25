package com.example.demo.controller;

import com.example.demo.dto.SaveAnswerRequest;
import com.example.demo.model.Answer;
import com.example.demo.service.AnswerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/answers")
@CrossOrigin(origins = "http://localhost:3000")
public class AnswerController {

    @Autowired
    private AnswerService service;

    /**
     * Saves or updates an answer for a specific exam attempt.
     * Accessible by students during their exam.
     */
    @PostMapping("/save")
    public Answer save(@Valid @RequestBody SaveAnswerRequest req) {
        return service.saveAnswer(
            req.attemptId(), 
            req.questionId(), 
            req.selectedOption(), 
            req.textAnswer()
        );
    }
}