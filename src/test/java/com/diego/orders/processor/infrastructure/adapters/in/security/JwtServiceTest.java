package com.diego.orders.processor.infrastructure.adapters.in.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtService.generateToken("diego");

        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractUsername() {
        String token = jwtService.generateToken("diego");

        String username = jwtService.extractUsername(token);

        assertEquals(username, "diego");
    }

    @Test
    void shouldValidateToken() {
        String token =
                jwtService.generateToken("diego");

        boolean valid = jwtService.isValid(token);

        assertTrue(valid);
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        boolean valid = jwtService.isValid("invalid-token");

        assertFalse(valid);
    }

    @Test
    void shouldReturnFalseForTamperedToken() {
        String token =
                jwtService.generateToken("diego");

        String tamperedToken =
                token.substring(0, token.length() - 2) + "xx";

        assertFalse(jwtService.isValid(tamperedToken));
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        assertThrows(
                JwtException.class,
                () -> jwtService.extractUsername("bad-token"));
    }
}