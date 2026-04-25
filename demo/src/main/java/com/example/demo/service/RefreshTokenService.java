package com.example.demo.service;

import com.example.demo.model.RefreshToken;
import com.example.demo.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    // 7 days in seconds
    private static final long REFRESH_TOKEN_EXPIRY_SECONDS = 7L * 24 * 60 * 60;

    @Autowired
    private RefreshTokenRepository repo;

    /**
     * Creates a new refresh token for the given user.
     * Uses UUID for unpredictability — not guessable or forgeable.
     */
    public RefreshToken create(int userId) {
        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUserId(userId);
        token.setExpiresAt(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRY_SECONDS));
        return repo.save(token);
    }

    /**
     * Validates token: must exist in DB and not be expired.
     * Throws RuntimeException (caught by GlobalExceptionHandler) if invalid.
     */
    public RefreshToken verify(String tokenStr) {
        RefreshToken token = repo.findByToken(tokenStr)
                .orElseThrow(() -> new RuntimeException("Refresh token not found — please log in again"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            repo.delete(token);  // Clean up expired token
            throw new RuntimeException("Refresh token expired — please log in again");
        }

        return token;
    }

    /**
     * Rotates the refresh token — old one deleted, new one issued.
     * Rotation prevents a stolen token from being used more than once.
     */
    public RefreshToken rotate(RefreshToken oldToken) {
        repo.delete(oldToken);
        return create(oldToken.getUserId());
    }

    public void deleteAllForUser(int userId) {
        repo.deleteByUserId(userId);
    }
}
