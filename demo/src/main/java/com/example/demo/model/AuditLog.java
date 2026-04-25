package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
@Data
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int logId;

    private int userId;
    
    @Column(nullable = false)
    private String action;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    private String ipAddress;
    
    private Instant timestamp;
}
