package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "results")
@Data
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int resultId;

    @ManyToOne
    @JoinColumn(name = "attempt_id")
    private ExamAttempt attempt;

    private int totalMarks;
    private int obtainedMarks;
}