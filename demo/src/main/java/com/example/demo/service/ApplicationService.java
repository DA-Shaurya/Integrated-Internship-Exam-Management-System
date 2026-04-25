package com.example.demo.service;

import com.example.demo.model.Application;
import com.example.demo.model.Internship;
import com.example.demo.model.User;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.InternshipRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepo;

    @Autowired
    private InternshipRepository internshipRepo;

    @Autowired
    private UserRepository userRepo;

    /**
     * Handles internship application logic.
     * Enforces CGPA requirements and prevents duplicate applications.
     */
    @Transactional
    public Application apply(int userId, int internshipId, String resumeUrl, Double cgpa) {
        Internship internship = internshipRepo.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        User student = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Enforce Eligibility
        if (cgpa != null && cgpa < internship.getMinCgpa()) {
            throw new RuntimeException("Min CGPA requirement: " + internship.getMinCgpa());
        }

        // Prevent Duplicates
        if (applicationRepo.existsByStudent_UserIdAndInternship_InternshipId(userId, internshipId)) {
            throw new RuntimeException("You have already applied for this internship.");
        }

        Application application = new Application();
        application.setStudent(student);
        application.setInternship(internship);
        application.setStatus("APPLIED");
        application.setResumeUrl(resumeUrl);
        application.setAppliedAt(Instant.now());
        application.setCgpaAtApply(cgpa);

        return applicationRepo.save(application);
    }

    /**
     * Updates application status. Restricted to ADMIN in controller.
     */
    @Transactional
    public Application updateStatus(int applicationId, String newStatus) {
        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        List<String> validStatuses = List.of("APPLIED", "SHORTLISTED", "REJECTED", "HIRED");
        if (!validStatuses.contains(newStatus.toUpperCase())) {
            throw new RuntimeException("Invalid status transition: " + newStatus);
        }

        application.setStatus(newStatus.toUpperCase());
        return applicationRepo.save(application);
    }

    public List<Application> getStudentApplications(int userId) {
        return applicationRepo.findByStudent_UserId(userId);
    }

    public Page<Application> getAllApplicationsPaginated(String status, Pageable pageable) {
        if (status != null && !status.isEmpty()) {
            return applicationRepo.findByStatus(status.toUpperCase(), pageable);
        }
        return applicationRepo.findAll(pageable);
    }
}