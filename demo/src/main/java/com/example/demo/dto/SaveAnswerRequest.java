package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

public record SaveAnswerRequest(
    @NotNull(message = "Attempt ID is required")
    Integer attemptId,

    @NotNull(message = "Question ID is required")
    Integer questionId,

    Integer selectedOption, // Nullable for descriptive answers if we add them

    String textAnswer       // Optional text for non-MCQ questions
) {}
