package com.example.demo.service;

import com.example.demo.model.Application;
import com.example.demo.model.Internship;
import com.example.demo.model.User;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.InternshipRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepo;

    @Mock
    private InternshipRepository internshipRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private ApplicationService applicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void apply_eligibleStudent_savesApplication() {
        // Arrange
        int userId = 1;
        int internshipId = 100;
        
        User student = new User();
        student.setUserId(userId);
        
        Internship internship = new Internship();
        internship.setInternshipId(internshipId);
        internship.setMinCgpa(7.5);
        
        when(internshipRepo.findById(internshipId)).thenReturn(Optional.of(internship));
        when(userRepo.findById(userId)).thenReturn(Optional.of(student));
        when(applicationRepo.existsByStudent_UserIdAndInternship_InternshipId(userId, internshipId)).thenReturn(false);
        when(applicationRepo.save(any(Application.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Application result = applicationService.apply(userId, internshipId, "resume.pdf", 8.0);

        // Assert
        assertNotNull(result);
        assertEquals("APPLIED", result.getStatus());
        assertEquals(student, result.getStudent());
        verify(applicationRepo, times(1)).save(any(Application.class));
    }

    @Test
    void apply_lowCgpa_throwsException() {
        // Arrange
        int userId = 1;
        int internshipId = 100;
        
        Internship internship = new Internship();
        internship.setMinCgpa(9.0);
        
        when(internshipRepo.findById(internshipId)).thenReturn(Optional.of(internship));
        when(userRepo.findById(userId)).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            applicationService.apply(userId, internshipId, "resume.pdf", 8.0);
        });
    }
}
