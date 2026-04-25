package com.example.demo.repository;

import com.example.demo.model.Internship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Integer> {

    Page<Internship> findByIsActiveTrue(Pageable pageable);

    Page<Internship> findByDomainIgnoreCaseAndIsActiveTrue(String domain, Pageable pageable);

    @Query("SELECT i FROM Internship i WHERE i.minCgpa <= :cgpa AND i.isActive = true")
    Page<Internship> findEligibleInternships(@Param("cgpa") double cgpa, Pageable pageable);

    long countByIsActiveTrue();
}