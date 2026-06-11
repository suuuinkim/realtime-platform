package com.practice.realtimeplatform.auth.service;

import com.practice.realtimeplatform.auth.dto.LoginRequestDTO;
import com.practice.realtimeplatform.auth.dto.LoginResponseDTO;
import com.practice.realtimeplatform.global.security.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final String id = "admin";
    private final String pwd = "1234";

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    private String encodePwd;

    @PostConstruct
    public void init() {
        encodePwd = bCryptPasswordEncoder.encode(pwd);
    }

    public AuthService(BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 濡쒓렇??
     * @param loginRequest
     * @return
     * @throws Exception
     */
    public LoginResponseDTO login(LoginRequestDTO loginRequest) throws Exception {
        if (!loginRequest.getLoginId().equals(id)) {
            throw new Exception("?꾩씠?붽? ?쇱튂?섏? ?딆뒿?덈떎");
        }

        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), encodePwd)) {
            throw new Exception("鍮꾨?踰덊샇媛 ?쇱튂?섏? ?딆뒿?덈떎.");
        }

        String accessToken = jwtUtil.generateAccessToken(id);
        String refreshToken = jwtUtil.generateRefreshToken(id);

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setAccessToken(accessToken);
        loginResponseDTO.setRefreshToken(refreshToken);

        return loginResponseDTO;
    }
}
