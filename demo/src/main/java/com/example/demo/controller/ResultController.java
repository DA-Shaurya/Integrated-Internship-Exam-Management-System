package com.example.demo.controller;

import com.example.demo.model.Result;
import com.example.demo.repository.ResultRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Results", description = "Endpoints for students to view their exam performance")
public class ResultController {

    @Autowired
    private ResultRepository resultRepo;

    @GetMapping("/my")
    @Operation(summary = "Get my exam results", description = "Returns a list of exam results for the currently authenticated student", security = @SecurityRequirement(name = "bearerAuth"))
    public List<ResultResponse> getMyResults() {
        int userId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
        
        return resultRepo.findAll().stream()
                .filter(r -> r.getAttempt().getUserId() == userId)
                .map(ResultResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public record ResultResponse(
        int resultId,
        String examName,
        java.time.Instant attemptDate,
        int obtainedMarks,
        int totalMarks,
        double percentage,
        String status
    ) {
        public static ResultResponse fromEntity(Result r) {
            double pct = (r.getTotalMarks() > 0) ? (r.getObtainedMarks() * 100.0 / r.getTotalMarks()) : 0;
            return new ResultResponse(
                r.getResultId(),
                r.getAttempt().getExam().getExamName(),
                r.getAttempt().getStartTime(),
                r.getObtainedMarks(),
                r.getTotalMarks(),
                pct,
                pct >= 60 ? "PASS" : "FAIL"
            );
        }
    }
}
