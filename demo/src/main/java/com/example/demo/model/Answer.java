package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "answers")
@Data
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int answerId;

    @ManyToOne
    @JoinColumn(name = "attempt_id")
    private ExamAttempt attempt;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private Integer selectedOption;

    private String descriptiveAnswer;
}