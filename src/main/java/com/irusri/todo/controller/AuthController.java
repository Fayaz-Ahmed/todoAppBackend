package com.irusri.todo.controller;

import com.irusri.todo.dto.UserDto;
import com.irusri.todo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String register(@Valid @RequestBody UserDto userDto) {
        return authService.register(userDto);
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody UserDto userDto) {
        return authService.login(userDto);
    }
}