package com.example.demo.controller;

import com.example.demo.dto.StudentProfileResponse;
import com.example.demo.model.Student;
import com.example.demo.model.User;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Student Profiles", description = "Endpoints for students to manage their academic and professional profiles")
public class StudentController {

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/profile")
    @Operation(summary = "Get my profile", description = "Returns the profile of the currently authenticated student", security = @SecurityRequirement(name = "bearerAuth"))
    public StudentProfileResponse getMyProfile() {
        int userId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
        Student student = studentRepo.findByUser_UserId(userId);
        if (student == null) {
            // Create a blank student profile if it doesn't exist yet
            User user = userRepo.findById(userId).orElseThrow();
            student = new Student();
            student.setUser(user);
            student = studentRepo.save(student);
        }
        return StudentProfileResponse.fromEntity(student);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update my profile", description = "Updates personal and academic info for the authenticated student", security = @SecurityRequirement(name = "bearerAuth"))
    public StudentProfileResponse updateProfile(@RequestBody Map<String, Object> data) {
        int userId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
        Student student = studentRepo.findByUser_UserId(userId);
        
        if (data.containsKey("name")) {
            User user = student.getUser();
            user.setName(data.get("name").toString());
            userRepo.save(user);
        }
        
        if (data.containsKey("phone")) student.setPhone(data.get("phone").toString());
        if (data.containsKey("cgpa")) student.setCgpa(Double.parseDouble(data.get("cgpa").toString()));
        
        if (data.containsKey("skills")) {
            List<String> skills = (List<String>) data.get("skills");
            student.setSkills(String.join(",", skills));
        }
        
        return StudentProfileResponse.fromEntity(studentRepo.save(student));
    }
}
