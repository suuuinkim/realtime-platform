package com.practice.realtimeplatform.service;

import com.practice.realtimeplatform.dto.LoginRequestDTO;
import com.practice.realtimeplatform.dto.LoginResponseDTO;
import com.practice.realtimeplatform.util.JwtUtil;
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
     * 로그인
     * @param loginRequest
     * @return
     * @throws Exception
     */
    public LoginResponseDTO login(LoginRequestDTO loginRequest) throws Exception {
        if (!loginRequest.getLoginId().equals(id)) {
            throw new Exception("아이디가 일치하지 않습니다");
        }

        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), encodePwd)) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(id);
        String refreshToken = jwtUtil.generateRefreshToken(id);

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setAccessToken(accessToken);
        loginResponseDTO.setRefreshToken(refreshToken);

        return loginResponseDTO;
    }
}
