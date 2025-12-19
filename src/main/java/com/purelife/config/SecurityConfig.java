package com.purelife.config;

import com.purelife.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 設定
 */
@Configuration //告訴Spring這個類別是設定類別
@EnableWebSecurity //啟用Spring Security的網路安全功能
@RequiredArgsConstructor //自動生成建構子注入jwtAuthenticationFilter
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 關閉 CSRF（因為用 JWT，不需要 CSRF）
            .csrf(AbstractHttpConfigurer::disable)
            
            // 設定 Session 為無狀態（JWT 不需要 Session）
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 設定哪些路徑需要認證
            .authorizeHttpRequests(auth -> auth
                // 公開的 API（不需要登入）
                .requestMatchers("/api/auth/**").permitAll()           // 登入、註冊
                .requestMatchers("/api/products/**").permitAll()       // 商品查詢
                .requestMatchers("/api/faqs/**").permitAll()           // FAQ
                .requestMatchers(HttpMethod.GET, "/api/**").permitAll() // 所有 GET 請求
                
                // Swagger 文件
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                
                // 其他請求都需要登入
                .anyRequest().authenticated()
            )
            
            // 加入 JWT 過濾器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}