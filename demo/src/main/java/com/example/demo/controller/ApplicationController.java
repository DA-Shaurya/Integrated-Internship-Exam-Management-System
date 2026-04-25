package com.example.demo.controller;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.model.Application;
import com.example.demo.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Applications", description = "Endpoints for students to apply for internships and view status")
public class ApplicationController {

    @Autowired
    private ApplicationService service;

    /**
     * Student applies for an internship.
     */
    @PostMapping("/apply")
    @Operation(summary = "Apply for an internship", description = "Submits a new internship application for the student", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Application submitted successfully")
    public Application apply(@RequestBody Map<String, Object> data) {
        int studentId = (int) data.get("studentId");
        int internshipId = (int) data.get("internshipId");
        String resumeUrl = (String) data.get("resumeUrl");
        
        // Handle CGPA — can be Integer or Double from JSON
        Double cgpa = data.get("cgpa") != null ? Double.valueOf(data.get("cgpa").toString()) : null;

        return service.apply(studentId, internshipId, resumeUrl, cgpa);
    }

    /**
     * Admin updates application status.
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update application status", description = "Changes the status of an application (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    public Application updateStatus(@PathVariable int id, @RequestBody Map<String, String> data) {
        return service.updateStatus(id, data.get("status"));
    }

    /**
     * Get all applications for a specific student.
     */
    /**
     * Get applications for the logged-in student.
     */
    @GetMapping("/my")
    @Operation(summary = "Get my applications", description = "Returns a list of applications for the currently authenticated student", security = @SecurityRequirement(name = "bearerAuth"))
    public List<ApplicationResponse> getMyApplications() {
        int userId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName()); 
        
        return service.getStudentApplications(userId).stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student applications", description = "Returns a list of applications for a specific student", security = @SecurityRequirement(name = "bearerAuth"))
    public List<Application> getStudentApplications(@PathVariable int studentId) {
        return service.getStudentApplications(studentId);
    }
}