package com.purelife.controller;

import com.purelife.controller.dto.request.LoginRequest;
import com.purelife.controller.dto.request.RegisterRequest;
import com.purelife.controller.dto.response.ApiResponse;
import com.purelife.controller.dto.response.LoginResponse;
import com.purelife.entity.Member;
import com.purelife.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

//告訴Spring：這是一個REST API Controller
//@RequestBody 把JSON轉成LoginRequest物件
//@Valid驗證（格式驗證，不用查詢資料庫）
@RestController //= @Controller + @ResponseBody
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ApiResponse<Member> register(@Valid @RequestBody RegisterRequest request) {
        //格式驗證成功才會呼叫service執行業務邏輯驗證
        Member member = authService.register(request);
        return ApiResponse.success("註冊成功", member);
    }
    
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ApiResponse.success("登入成功", loginResponse);
    }
}