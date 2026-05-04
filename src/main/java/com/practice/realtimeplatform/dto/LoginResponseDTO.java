package com.practice.realtimeplatform.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
}
