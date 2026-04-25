package com.example.demo.controller;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.service.ApplicationService;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.InternshipRepository;
import com.example.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Management", description = "Endpoints for platform administration")
public class AdminController {

    @Autowired
    private ApplicationService appService;

    @Autowired
    private com.example.demo.service.InternshipService internshipService;

    @Autowired
    private ApplicationRepository appRepo;

    @Autowired
    private InternshipRepository internshipRepo;

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/stats")
    @Operation(summary = "Get platform metrics", description = "Returns total counts for KPI cards (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public Map<String, Long> getStats() {
        return Map.of(
            "totalApplications", appRepo.count(),
            "activeInternships", internshipRepo.countByIsActiveTrue(),
            "totalStudents", userRepo.countByRole(com.example.demo.model.Role.STUDENT),
            "pendingReviews", appRepo.countByStatus("APPLIED")
        );
    }
    @GetMapping("/applications")
    @Operation(summary = "Get paginated applications", description = "Returns a paginated list of all student applications (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public Page<ApplicationResponse> getApplications(
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return appService.getAllApplicationsPaginated(status, pageable)
                .map(ApplicationResponse::fromEntity);
    }

    @DeleteMapping("/internships/{id}")
    @Operation(summary = "Soft delete an internship", description = "Sets is_deleted = true for the given internship ID (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Internship soft deleted successfully")
    public void deleteInternship(@PathVariable int id) {
        internshipService.softDeleteInternship(id);
    }
}
