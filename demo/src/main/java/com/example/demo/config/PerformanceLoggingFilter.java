package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Performance interceptor to log API execution times.
 * Critical for identifying bottlenecks in a production SaaS.
 */
@Component
@Slf4j
public class PerformanceLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            // Only log performance for API calls
            if (request.getRequestURI().startsWith("/api")) {
                log.info("API {} {} - {}ms - Status: {}", 
                        request.getMethod(), 
                        request.getRequestURI(), 
                        duration, 
                        response.getStatus());
                
                // Alert if request is slow (> 500ms)
                if (duration > 500) {
                    log.warn("SLOW API detected: {} took {}ms", request.getRequestURI(), duration);
                }
            }
        }
    }
}
