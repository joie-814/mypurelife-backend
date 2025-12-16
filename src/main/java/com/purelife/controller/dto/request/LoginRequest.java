package com.purelife.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank(message = "帳號不能為空")
    private String account;
    
    @NotBlank(message = "密碼不能為空")
    private String password;
}
