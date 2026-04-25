package com.example.demo.dto;

import com.example.demo.model.Internship;
import java.time.LocalDate;

public record InternshipResponse(
    int internshipId,
    String role,
    String domain,
    String description,
    double minCgpa,
    double stipend,
    LocalDate deadline,
    String companyName,
    String location
) {
    public static InternshipResponse fromEntity(Internship i) {
        return new InternshipResponse(
            i.getInternshipId(),
            i.getRole(),
            i.getDomain(),
            i.getDescription(),
            i.getMinCgpa(),
            i.getStipend(),
            i.getDeadline(),
            i.getCompany() != null ? i.getCompany().getCompanyName() : "N/A",
            i.getCompany() != null ? i.getCompany().getLocation() : "N/A"
        );
    }
}
