package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The opaque random token string stored in the HttpOnly cookie
    @Column(nullable = false, unique = true, length = 512)
    private String token;

    // Which user this refresh token belongs to
    @Column(nullable = false)
    private int userId;

    // Hard expiry — after this, the token is permanently invalid
    @Column(nullable = false)
    private Instant expiresAt;
}
