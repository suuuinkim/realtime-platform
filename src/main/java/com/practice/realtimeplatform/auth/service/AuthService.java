package com.practice.realtimeplatform.auth.service;

import com.practice.realtimeplatform.auth.dto.LoginRequestDTO;
import com.practice.realtimeplatform.auth.dto.LoginResponseDTO;
import com.practice.realtimeplatform.global.security.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final String id;
    private final String pwd;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    private String encodePwd;

    @PostConstruct
    public void init() {
        encodePwd = bCryptPasswordEncoder.encode(pwd);
    }

    public AuthService(BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil,
                       @Value("${app.admin.username:admin}") String id,
                       @Value("${app.admin.password:1234}") String pwd) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
        this.id = id;
        this.pwd = pwd;
    }

    /**
     * 로그인
     * @param loginRequest
     * @return
     * @throws Exception
     */
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        if (!id.equals(loginRequest.getLoginId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), encodePwd)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(id);
        String refreshToken = jwtUtil.generateRefreshToken(id);

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setAccessToken(accessToken);
        loginResponseDTO.setRefreshToken(refreshToken);

        return loginResponseDTO;
    }
}
