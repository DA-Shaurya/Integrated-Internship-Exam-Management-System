package com.example.demo.repository;

import com.example.demo.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    boolean existsByStudent_UserIdAndInternship_InternshipId(int userId, int internshipId);

    List<Application> findByStudent_UserId(int userId);

    Page<Application> findAll(Pageable pageable);

    Page<Application> findByStatus(String status, Pageable pageable);

    long countByStatus(String status);
}