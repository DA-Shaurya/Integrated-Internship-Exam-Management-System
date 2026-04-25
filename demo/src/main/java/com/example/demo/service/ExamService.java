package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ExamService {

    @Autowired
    private ExamAttemptRepository attemptRepo;

    @Autowired
    private ExamRepository examRepo;

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private AnswerRepository answerRepo;

    @Autowired
    private ResultRepository resultRepo;

    @Autowired
    private OptionRepository optionRepo;

    @Transactional
    public ExamAttempt startExam(int userId, int examId) {
        log.info("Student {} starting exam {}", userId, examId);
        
        Exam exam = examRepo.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        Optional<ExamAttempt> existing = attemptRepo.findAll().stream()
                .filter(a -> a.getUserId() == userId && a.getExam().getExamId() == examId && "IN_PROGRESS".equals(a.getStatus()))
                .findFirst();

        if (existing.isPresent()) {
            log.info("Resuming existing attempt {} for student {}", existing.get().getAttemptId(), userId);
            return existing.get();
        }

        ExamAttempt attempt = new ExamAttempt();
        attempt.setUserId(userId);
        attempt.setExam(exam);
        attempt.setStatus("IN_PROGRESS");
        attempt.setStartTime(Instant.now());

        return attemptRepo.save(attempt);
    }

    @Transactional
    public Result submitExam(int attemptId) {
        log.info("Submitting exam attempt {}", attemptId);
        
        ExamAttempt attempt = attemptRepo.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if ("COMPLETED".equals(attempt.getStatus())) {
            log.warn("Attempt {} already completed, rejecting duplicate submission", attemptId);
            throw new RuntimeException("Exam already submitted");
        }

        Instant now = Instant.now();
        List<Answer> answers = answerRepo.findByAttempt_AttemptId(attemptId);

        int totalMarks = 0;
        int obtainedMarks = 0;

        for (Answer ans : answers) {
            totalMarks += ans.getQuestion().getMarks();
            if (ans.getSelectedOption() != null) {
                Option opt = optionRepo.findById(ans.getSelectedOption()).orElse(null);
                if (opt != null && opt.isCorrect()) {
                    obtainedMarks += ans.getQuestion().getMarks();
                }
            }
        }

        attempt.setStatus("COMPLETED");
        attempt.setSubmittedAt(now);
        attemptRepo.save(attempt);

        Result result = new Result();
        result.setAttempt(attempt);
        result.setTotalMarks(totalMarks);
        result.setObtainedMarks(obtainedMarks);

        log.info("Exam submitted successfully. Attempt: {}, Score: {}/{}", attemptId, obtainedMarks, totalMarks);
        return resultRepo.save(result);
    }

    public List<Question> getQuestionsForExam(int examId) {
        log.debug("Fetching and shuffling questions for exam {}", examId);
        List<Question> questions = questionRepo.findByExam_ExamId(examId);
        java.util.Collections.shuffle(questions);
        return questions;
    }
}