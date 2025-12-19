package com.purelife.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 是一個「發會員識別證、驗證會員識別證、讀識別證內容」的工具
 * JWT 工具類，JWT = 會員的「數位身分證」
 * 負責產生、解析、驗證 JWT Token
 */
@Component
public class JwtUtil {

    // JWT 密鑰（印章）：只有後端知道，用來「簽名 JWT」，防止別人偽造 Token
    @Value("${jwt.secret}") // @Value 把設定檔的值注入成變數(把設定檔裡 jwt.secret 的值，塞進這個變數)
    private String secret;

    // JWT 有效期限（毫秒）
    @Value("${jwt.expiration}") //去application.properties / application.yml 找 jwt.expiration 這個key
    private long expiration;

    /**
     * 產生 JWT Token
     */
    public String generateToken(Integer memberId, String account) {
        Date now = new Date(); // 現在時間
        Date expiryDate = new Date(now.getTime() + expiration); // 過期時間

        return Jwts.builder()
                   .subject(memberId.toString())     // 「你是誰」→ 會員ID
                   .claim("account", account)  // 額外資訊：帳號
                   .issuedAt(now)                    // 發行時間
                   .expiration(expiryDate)           // 簽名過期時間
                   .signWith(getSigningKey())       // 發token、蓋章（用 secret）
                   .compact();                      // 壓縮成字串(回傳的那一串字，就是 JWT Token)
    }

    /**
     * 從 Token 取得會員 ID:把 JWT 拆開，拿出 subject，轉成會員 ID
     */
    public Integer getMemberIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Integer.parseInt(claims.getSubject());
    }

    /**
     * 從 Token 取得帳號:把 JWT 拆開，拿出 account 欄位
     */
    public String getAccountFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("account", String.class);
    }

    /**
     * 驗證 Token 是否有效(不是自己驗證，因為有可能是偽造的，所以交給 JWT 套件驗證)
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (SecurityException e) {
            System.err.println("無效的 JWT 簽名: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("無效的 JWT 格式: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT 已過期: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("不支援的 JWT: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT 為空: " + e.getMessage());
        }
        return false;
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                   .verifyWith(getSigningKey())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    //把設定檔裡的字串secret，轉成HMAC可以用的加密鑰匙
    //加密不能用 String、加密底層只吃 byte[]
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes); // 用 HMAC-SHA 演算法產生密鑰
    }
}