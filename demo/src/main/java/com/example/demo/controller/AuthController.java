package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.AuditLog;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.User;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and token refresh")
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refreshToken";
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7 days in seconds

    @Autowired private AuthService authService;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private RefreshTokenService refreshTokenService;
    @Autowired private UserRepository userRepository;
    @Autowired private AuditLogRepository auditRepo;

    // ── Register ─────────────────────────────────────────────────────────────
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns JWT tokens")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req,
                                                 HttpServletResponse response) {
        User user = new User();
        user.setName(req.name());
        user.setEmail(req.email().trim().toLowerCase());
        user.setPassword(req.password());
        user.setRole(req.role());

        User saved = authService.register(user);
        
        logAction(saved.getUserId(), "REGISTER", "User registered: " + saved.getEmail());
        
        return ResponseEntity.ok(issueTokens(saved, response));
    }

    // ── Login ────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns JWT tokens in response and HttpOnly cookie")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req,
                                              HttpServletResponse response) {
        User user = authService.login(req.email().trim().toLowerCase(), req.password());
        
        logAction(user.getUserId(), "LOGIN", "User logged in: " + user.getEmail());
        
        return ResponseEntity.ok(issueTokens(user, response));
    }

    // ── Refresh ──────────────────────────────────────────────────────────────
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Rotates refresh token cookie and issues a new access token")
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @ApiResponse(responseCode = "401", description = "Invalid or missing refresh token")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String tokenStr = extractRefreshCookie(request);
        if (tokenStr == null) {
            return ResponseEntity.status(401)
                .body(Map.of("message", "No refresh token — please log in"));
        }

        // Verify + rotate (old token deleted, new one issued → prevents replay attacks)
        RefreshToken oldToken = refreshTokenService.verify(tokenStr);
        RefreshToken newToken = refreshTokenService.rotate(oldToken);

        User user = userRepository.findById(oldToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getUserId());

        setRefreshCookie(response, newToken.getToken());

        return ResponseEntity.ok(new AuthResponse(
            accessToken, user.getUserId(), user.getName(), user.getEmail(), user.getRole()
        ));
    }

    // ── Logout ───────────────────────────────────────────────────────────────
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Invalidates refresh tokens and clears the auth cookie")
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String tokenStr = extractRefreshCookie(request);
        if (tokenStr != null) {
            try {
                RefreshToken token = refreshTokenService.verify(tokenStr);
                refreshTokenService.deleteAllForUser(token.getUserId());
            } catch (Exception ignored) {
                // Token already invalid — still clear the cookie
            }
        }
        clearRefreshCookie(response);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private AuthResponse issueTokens(User user, HttpServletResponse response) {
        // Delete any existing sessions for this user before issuing new ones
        refreshTokenService.deleteAllForUser(user.getUserId());

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getUserId());
        RefreshToken refreshToken = refreshTokenService.create(user.getUserId());

        setRefreshCookie(response, refreshToken.getToken());

        return new AuthResponse(accessToken, user.getUserId(), user.getName(), user.getEmail(), user.getRole());
    }

    private void setRefreshCookie(HttpServletResponse response, String tokenValue) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, tokenValue);
        cookie.setHttpOnly(true);      // ← Inaccessible to JavaScript (XSS protection)
        cookie.setSecure(false);       // Set true in production (requires HTTPS)
        cookie.setPath("/api/auth");   // Only sent to auth endpoints
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0); // Immediately expire
        response.addCookie(cookie);
    }

    private String extractRefreshCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void logAction(int userId, String action, String details) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setDetails(details);
        log.setTimestamp(java.time.Instant.now());
        auditRepo.save(log);
    }
}