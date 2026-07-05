package com.restaurant.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private static final String SECRET =
            "NWgK/7G7+UeO6ZxajPdlhnyS17uiZLTjQP2uYHrlJ1inuvllknan/CCysFhciujCLfGKfiL4hpq/BCnBj/mwCQ==";

    private JwtUtils jwtUtils;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(SECRET, 3_600_000L);
        userDetails = User.withUsername("daniel")
                .password("irrelevant")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void generatedTokenContainsUsername() {
        String token = jwtUtils.generateToken(userDetails);

        assertEquals("daniel", jwtUtils.extractUsername(token));
    }

    @Test
    void generatedTokenIsValidForItsUser() {
        String token = jwtUtils.generateToken(userDetails);

        assertTrue(jwtUtils.isTokenValid(token, userDetails));
    }

    @Test
    void tokenIsInvalidForDifferentUser() {
        String token = jwtUtils.generateToken(userDetails);
        UserDetails otherUser = User.withUsername("intruz")
                .password("irrelevant")
                .authorities("ROLE_USER")
                .build();

        assertFalse(jwtUtils.isTokenValid(token, otherUser));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtUtils expiringJwtUtils = new JwtUtils(SECRET, -1000L);
        String token = expiringJwtUtils.generateToken(userDetails);

        assertNull(expiringJwtUtils.extractUsername(token));
        assertFalse(expiringJwtUtils.isTokenValid(token, userDetails));
    }

    @Test
    void tamperedTokenIsRejected() {
        String token = jwtUtils.generateToken(userDetails) + "x";

        assertNull(jwtUtils.extractUsername(token));
        assertFalse(jwtUtils.isTokenValid(token, userDetails));
    }

    @Test
    void garbageTokenIsRejected() {
        assertNull(jwtUtils.extractUsername("not.a.jwt"));
    }
}
