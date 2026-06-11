package com.practice.realtimeplatform.auth.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
}
