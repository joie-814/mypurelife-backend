package com.purelife.controller.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminLoginResponse {
    private String token;
    private String tokenType;
    private Integer adminId;
    private String account;
    private String name;
    private String role;
}