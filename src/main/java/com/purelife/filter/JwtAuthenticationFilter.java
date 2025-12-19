package com.purelife.filter;

import com.purelife.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 認證過濾器：JWT 只存 memberId，沒有存完整會員資料
 * 每個請求都會經過這裡（不管是否登入都會），檢查是否有有效的 JWT Token
 * 如果請求有帶 JWT，而且是合法的，就幫 Spring 記住『這個人已登入』，若驗證失敗一律視為沒登入
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    //@NonNull Spring 保證不會傳 null，如果真的 null → 直接丟 NullPointerException
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. 從 Header 取得 Token
        String authHeader = request.getHeader("Authorization");
        
        // 2. 檢查是否有 Bearer Token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 沒有 Token，繼續執行（可能是不需要登入的 API）
            filterChain.doFilter(request, response);
            return;
        }
        
        // 3. 取出 Token（去掉 "Bearer " 前綴）
        // 前端傳送的是Authorization:Bearer eyJhbGciOiJIUzI1NiJ9...
        String token = authHeader.substring(7);
        
        // 4. 驗證 Token
        if (jwtUtil.validateToken(token)) {
            // 5. Token 有效，取得會員資訊
            Integer memberId = jwtUtil.getMemberIdFromToken(token);
            String account = jwtUtil.getAccountFromToken(token);
            
            // 6. 建立認證物件，放入SecurityContext
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            memberId,           // principal：存放 memberId
                            null,               // credentials：不需要
                            Collections.emptyList()  // authorities：權限列表（目前不用）
                    );
            
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            
            // 7. 設定到 SecurityContext（這樣後續就能取得登入資訊）
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        // 8. 繼續執行後續的 Filter
        filterChain.doFilter(request, response);
    }
}