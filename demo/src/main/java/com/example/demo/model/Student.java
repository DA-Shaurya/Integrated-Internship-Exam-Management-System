package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "students")
@Data
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studentId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private double cgpa;
    private String course;
    private String phone;
    private String skills; // Stored as JSON string or comma-separated
    private String resumeFilename;
}
