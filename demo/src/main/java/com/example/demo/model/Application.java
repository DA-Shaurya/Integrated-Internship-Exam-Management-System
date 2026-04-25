package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Table(name = "applications")
@Data
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int applicationId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "internship_id")
    private Internship internship;

    @Column(nullable = false)
    private String status;  // APPLIED, SHORTLISTED, REJECTED, HIRED

    @Column(length = 1024)
    private String resumeUrl; // Stores path or URL to the uploaded resume

    private Instant appliedAt;
    
    private Double cgpaAtApply; // Capture CGPA at time of application for history
}