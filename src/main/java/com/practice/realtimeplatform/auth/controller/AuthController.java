package com.practice.realtimeplatform.auth.controller;

import com.practice.realtimeplatform.auth.dto.LoginRequestDTO;
import com.practice.realtimeplatform.auth.dto.LoginResponseDTO;
import com.practice.realtimeplatform.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequest) throws Exception{
        LoginResponseDTO login = authService.login(loginRequest);

        return login;

    }
}
