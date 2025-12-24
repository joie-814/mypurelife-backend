package com.purelife.controller.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberResponse {
    private Integer memberId;
    private String account;
    private String name;
    private String email;
    private String phone;
    private String memberLevel;
    private LocalDateTime registrationTime;
}
