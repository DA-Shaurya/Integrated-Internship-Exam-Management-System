package com.example.demo.dto;

import com.example.demo.model.Application;
import java.time.Instant;

public record ApplicationResponse(
    int applicationId,
    int studentId,
    String studentName,
    String studentEmail,
    int internshipId,
    String internshipTitle,
    String companyName,
    String status,
    double stipend,
    Double cgpaAtApply,
    String resumeUrl,
    Instant appliedAt
) {
    public static ApplicationResponse fromEntity(Application a) {
        return new ApplicationResponse(
            a.getApplicationId(),
            a.getStudent().getUserId(),
            a.getStudent().getName(),
            a.getStudent().getEmail(),
            a.getInternship().getInternshipId(),
            a.getInternship().getRole(),
            a.getInternship().getCompany() != null ? a.getInternship().getCompany().getCompanyName() : "N/A",
            a.getStatus(),
            a.getInternship().getStipend(),
            a.getCgpaAtApply(),
            a.getResumeUrl(),
            a.getAppliedAt()
        );
    }
}
