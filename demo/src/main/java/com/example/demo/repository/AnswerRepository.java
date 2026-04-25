package com.example.demo.repository;

import com.example.demo.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    Optional<Answer> findByAttempt_AttemptIdAndQuestion_QuestionId(int attemptId, int questionId);

    java.util.List<Answer> findByAttempt_AttemptId(int attemptId);
}