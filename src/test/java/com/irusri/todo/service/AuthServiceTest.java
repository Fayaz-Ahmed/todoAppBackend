package com.irusri.todo.service;

import com.irusri.todo.dto.UserDto;
import com.irusri.todo.entity.User;
import com.irusri.todo.repository.UserRepository;
import com.irusri.todo.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");

        String result = authService.register(userDto);
        assertEquals("User registered successfully", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_UserAlreadyExists() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(RuntimeException.class, () -> authService.register(userDto));
        assertEquals("User already exists with this email", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateJwtToken(userDto.getEmail())).thenReturn("testToken");

        String token = authService.login(userDto);
        assertEquals("testToken", token);
    }

    @Test
    void testLogin_InvalidCredentials() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("wrongPassword");

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userDto.getPassword(), user.getPassword())).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> authService.login(userDto));
        assertEquals("Invalid credentials", exception.getMessage());
    }
}
