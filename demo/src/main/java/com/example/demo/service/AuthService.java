package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(User user) {
        log.info("Registering new user: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public User login(String email, String password) {
        log.debug("Login attempt for email: {}", email);
        
        User user = repo.findByEmailIgnoreCase(email)
            .orElseThrow(() -> {
                log.warn("Login failed: User not found - {}", email);
                return new RuntimeException("User not found");
            });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Login failed: Invalid password for - {}", email);
            throw new RuntimeException("Invalid password");
        }

        log.info("User logged in successfully: {}", email);
        return user;
    }
}