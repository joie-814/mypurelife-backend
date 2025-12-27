package com.purelife.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginRequest {

    @NotBlank(message = "請輸入帳號")
    private String account;

    @NotBlank(message = "請輸入密碼")
    private String password;
}
