package com.example.demo.service;

import com.example.demo.model.Answer;
import com.example.demo.model.ExamAttempt;
import com.example.demo.model.Question;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.repository.ExamAttemptRepository;
import com.example.demo.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository repo;

    @Autowired
    private ExamAttemptRepository attemptRepo;

    @Autowired
    private QuestionRepository questionRepo;

    public Answer saveAnswer(int attemptId, int questionId, Integer optionId, String text) {

        Answer answer = repo.findByAttempt_AttemptIdAndQuestion_QuestionId(attemptId, questionId)
                .orElse(new Answer());

        answer.setAttempt(attemptRepo.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found")));

        answer.setQuestion(questionRepo.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found")));

        answer.setSelectedOption(optionId);
        answer.setDescriptiveAnswer(text);

        return repo.save(answer);
    }
}