package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "questions")
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int questionId;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    private String questionText;
    private String type; // MCQ / SUBJECTIVE
    private int marks;

    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties("question")
    private java.util.List<Option> options;
}