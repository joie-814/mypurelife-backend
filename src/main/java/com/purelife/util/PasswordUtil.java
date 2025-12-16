//密碼加密工具，因為密碼不能明文儲存在資料庫
package com.purelife.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    
    private static final int SALT_LENGTH = 16;
    
    // 生成隨機 salt（鹽）：一段隨機字串，每個使用者都不同
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH]; // 16 個隨機位元組
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    // 加密密碼（使用 SHA-256 + salt）
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword); //把位元組陣列轉換成字串
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密碼加密失敗", e);
        }
    }
    
    // 驗證密碼
    public static boolean verifyPassword(String password, String salt, String hashedPassword) {
        String newHash = hashPassword(password, salt);
        return newHash.equals(hashedPassword);
    }
}
