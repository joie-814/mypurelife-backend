package com.purelife.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateMemberRequest {
    
    @NotBlank(message = "姓名不能為空")
    private String name;
    
    private String phone;
}
