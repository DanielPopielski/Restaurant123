package com.restaurant.service;

import com.restaurant.dto.AuthenticationRequest;
import com.restaurant.dto.AuthenticationResponse;
import com.restaurant.dto.RegisterRequest;
import com.restaurant.entity.UserEntity;
import com.restaurant.enums.Role;
import com.restaurant.exception.ConflictException;
import com.restaurant.repository.UserRepository;
import com.restaurant.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private UserDetails userDetails(String username) {
        return User.withUsername(username).password("hashed").authorities("ROLE_USER").build();
    }

    @Test
    void registerSavesUserWithEncodedPasswordAndUserRole() {
        when(userRepository.existsByUsername("daniel")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userDetailsService.loadUserByUsername("daniel")).thenReturn(userDetails("daniel"));
        when(jwtUtils.generateToken(any())).thenReturn("jwt-token");

        AuthenticationResponse response =
                authService.register(new RegisterRequest("daniel", "password123"));

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertEquals("hashed", captor.getValue().getPassword());
        assertEquals(Role.USER, captor.getValue().getRole());
        assertEquals("jwt-token", response.token());
    }

    @Test
    void registerRejectsTakenUsername() {
        when(userRepository.existsByUsername("daniel")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> authService.register(new RegisterRequest("daniel", "password123")));
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticateReturnsToken() {
        when(userDetailsService.loadUserByUsername("daniel")).thenReturn(userDetails("daniel"));
        when(jwtUtils.generateToken(any())).thenReturn("jwt-token");

        AuthenticationResponse response =
                authService.authenticate(new AuthenticationRequest("daniel", "password123"));

        verify(authenticationManager).authenticate(any());
        assertEquals("jwt-token", response.token());
    }
}
