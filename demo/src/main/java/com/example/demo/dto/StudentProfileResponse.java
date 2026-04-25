package com.example.demo.dto;

import com.example.demo.model.Student;
import java.util.List;
import java.util.Arrays;

public record StudentProfileResponse(
    int userId,
    String name,
    String email,
    String phone,
    double cgpa,
    List<String> skills,
    String resumeFilename
) {
    public static StudentProfileResponse fromEntity(Student s) {
        List<String> skillsList = (s.getSkills() != null && !s.getSkills().isEmpty()) 
            ? Arrays.asList(s.getSkills().split(",")) 
            : List.of();
            
        return new StudentProfileResponse(
            s.getUser().getUserId(),
            s.getUser().getName(),
            s.getUser().getEmail(),
            s.getPhone(),
            s.getCgpa(),
            skillsList,
            s.getResumeFilename()
        );
    }
}
