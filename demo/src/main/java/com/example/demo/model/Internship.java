package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "internships")
@Data
@org.hibernate.annotations.SQLRestriction("is_deleted = false")
public class Internship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int internshipId;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String domain; // e.g. Frontend, Backend, AI

    @Column(columnDefinition = "TEXT")
    private String description;

    private double stipend;

    private double minCgpa;

    private LocalDate deadline;
    
    private boolean isActive = true;

    private boolean isDeleted = false;
}