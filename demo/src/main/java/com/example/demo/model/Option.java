package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "options")
@Data
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int optionId;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String optionText;
    private boolean isCorrect;
}