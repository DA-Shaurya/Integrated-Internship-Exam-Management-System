package com.example.demo.controller;

import com.example.demo.dto.InternshipResponse;
import com.example.demo.model.Internship;
import com.example.demo.service.InternshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internships")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Internships", description = "Endpoints for managing and viewing internship listings")
public class InternshipController {

    @Autowired
    private InternshipService service;

    /**
     * Admin creates a new internship listing.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a new internship", description = "Creates a new internship listing (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Internship added successfully")
    public Internship addInternship(@RequestBody Internship internship) {
        return service.addInternship(internship);
    }

    /**
     * Public/Student: Get all active internships.
     * Supports optional domain and cgpa filtering.
     */
    @GetMapping
    @Operation(summary = "Get paginated internships", description = "Returns a paginated list of internships with optional domain and cgpa filtering")
    public Page<InternshipResponse> getInternships(
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) Double cgpa,
            Pageable pageable) {
        
        // Note: For simplicity in the prototype, we filter then paginate in memory if using custom logic,
        // but ideally this is handled at the Repository level.
        return service.getAllInternshipsPaginated(domain, cgpa, pageable)
                .map(InternshipResponse::fromEntity);
    }
}