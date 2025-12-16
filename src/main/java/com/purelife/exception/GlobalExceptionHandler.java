package com.purelife.exception;

import io.swagger.v3.oas.annotations.Hidden;
import com.purelife.controller.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden // 隱藏在 Swagger 文件中
@RestControllerAdvice //這個類別是全域的錯誤處理器
public class GlobalExceptionHandler {

    // 處理處理 @Valid 驗證錯誤（例如：密碼長度不足）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("驗證失敗");
        
        return ApiResponse.error(errorMessage);
    }

    // 處理業務異常（例如：帳號已存在）
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleBusinessException(BusinessException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    // 處理資源找不到（例如：商品不存在）
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Object> handleNotFoundException(ResourceNotFoundException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    // 處理所有其他異常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return ApiResponse.error("系統錯誤：" + ex.getMessage());
    }
}