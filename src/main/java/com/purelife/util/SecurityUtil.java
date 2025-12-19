package com.purelife.util;

import com.purelife.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具類
 * 取得當前登入的會員資訊
 */
public class SecurityUtil {

    /**
     * 取得當前登入的會員 ID
     * @return 會員 ID
     * @throws BusinessException 如果未登入
     */
    public static Integer getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("請先登入");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof Integer) {
            return (Integer) principal;
        }
        
        throw new BusinessException("請先登入");
    }
    
    /**
     * 檢查是否已登入
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null 
                && authentication.isAuthenticated() 
                && authentication.getPrincipal() instanceof Integer;
    }
}
