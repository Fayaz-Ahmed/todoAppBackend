package com.irusri.todo.service;

import com.irusri.todo.dto.UserDto;
import com.irusri.todo.entity.User;
import com.irusri.todo.exception.UserAlreadyExistsException;
import com.irusri.todo.exception.UserNotFoundException;
import com.irusri.todo.exception.InvalidCredentialsException;
import com.irusri.todo.repository.UserRepository;
import com.irusri.todo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String register(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            logger.error("Registration failed: User already exists with email {}", userDto.getEmail());
            throw new UserAlreadyExistsException("User already exists with this email");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        userRepository.save(user);
        logger.info("User registered successfully with email {}", userDto.getEmail());
        return "User registered successfully";
    }

    public String login(UserDto userDto) {
        User existingUser = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> {
                    logger.error("Login failed: User not found with email {}", userDto.getEmail());
                    return new UserNotFoundException("User not found");
                });

        if (passwordEncoder.matches(userDto.getPassword(), existingUser.getPassword())) {
            String token = jwtUtil.generateJwtToken(existingUser.getEmail());
            logger.info("User logged in successfully with email {}", userDto.getEmail());
            return token;
        } else {
            logger.error("Login failed: Invalid credentials for email {}", userDto.getEmail());
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }
}
