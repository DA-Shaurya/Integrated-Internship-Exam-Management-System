package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "exam_attempts")
@Data
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int attemptId;

    private int userId;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    private String status;

    private java.time.Instant startTime;
    
    private java.time.Instant submittedAt;
}