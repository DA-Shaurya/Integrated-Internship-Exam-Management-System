package com.example.demo.dto;

import com.example.demo.model.Role;

// What we return to the client — never expose the hashed password
public record AuthResponse(
    String token,
    int userId,
    String name,
    String email,
    Role role
) {}
