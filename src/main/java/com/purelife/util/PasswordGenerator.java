package com.purelife.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";  // 你的密碼
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("原始密碼: " + rawPassword);
        System.out.println("加密後密碼: " + encodedPassword);
    }
}