package com.example.demo.service;

import com.example.demo.model.Internship;
import com.example.demo.repository.InternshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternshipService {

    @Autowired
    private InternshipRepository repo;

    public Internship addInternship(Internship internship) {
        return repo.save(internship);
    }

    public List<Internship> getAllInternships() {
        return repo.findByIsActiveTrue(Pageable.unpaged()).getContent();
    }

    public List<Internship> getByDomain(String domain) {
        return repo.findByDomainIgnoreCaseAndIsActiveTrue(domain, Pageable.unpaged()).getContent();
    }

    public List<Internship> getEligible(double cgpa) {
        // Legacy method for backward compatibility if needed, but we'll use paginated mostly
        return repo.findEligibleInternships(cgpa, Pageable.unpaged()).getContent();
    }

    public Page<Internship> getAllInternshipsPaginated(String domain, Double cgpa, Pageable pageable) {
        if (domain != null && !domain.isEmpty()) {
            return repo.findByDomainIgnoreCaseAndIsActiveTrue(domain, pageable);
        }
        if (cgpa != null) {
            return repo.findEligibleInternships(cgpa, pageable);
        }
        return repo.findByIsActiveTrue(pageable);
    }

    @org.springframework.transaction.annotation.Transactional
    public void softDeleteInternship(int id) {
        Internship internship = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        internship.setDeleted(true);
        repo.save(internship);
    }
}