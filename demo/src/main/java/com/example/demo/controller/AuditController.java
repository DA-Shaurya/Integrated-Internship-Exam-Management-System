package com.example.demo.controller;

import com.example.demo.model.AuditLog;
import com.example.demo.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Audit Logs", description = "Endpoints for platform monitoring and security auditing")
public class AuditController {

    @Autowired
    private AuditLogRepository auditLogRepo;

    /**
     * Logs a system action. Accessible by any authenticated user.
     */
    @PostMapping
    @Operation(summary = "Log an event", description = "Allows students to log critical events (e.g., tab switching) for anti-cheat purposes", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Log entry created")
    public AuditLog logAction(@RequestBody AuditLog log, HttpServletRequest request) {
        log.setTimestamp(Instant.now());
        log.setIpAddress(getClientIp(request));
        return auditLogRepo.save(log);
    }

    /**
     * Retrieves all logs. Restricted to ADMIN users only.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all audit logs", description = "Returns a complete list of system audit logs (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Logs retrieved successfully")
    public List<AuditLog> getLogs() {
        return auditLogRepo.findAll();
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
