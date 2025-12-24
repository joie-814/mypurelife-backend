package com.purelife.controller.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    
    @NotBlank(message = "請輸入目前密碼")
    private String currentPassword;
    
    @NotBlank(message = "請輸入新密碼")
    @Size(min = 6, message = "密碼至少需要 6 個字元")
    private String newPassword;
    
    @NotBlank(message = "請確認新密碼")
    private String confirmPassword;
}
