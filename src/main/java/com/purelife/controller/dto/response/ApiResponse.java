// 統一回傳格式（讓前端知道成功或失敗）

package com.purelife.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //自動生成無參數建構子
@AllArgsConstructor //自動生成包含所有參數的建構子
public class ApiResponse<T> {
    private boolean success;      // 是否成功
    private String message;       // 訊息
    private T data;              // 資料(型別由泛型決定)

    //省略這段建構子，因為有@NoArgsConstructor
    //public ApiResponse() {}
    //JSON反序列化需要無參數建構子，因為new ApiResponse()時不帶參數，用set把值放進去（JSON可能欄位不齊、順序不固定、data是不同型別...）

    //省略這段建構子，因為有@AllArgsConstructor
    //public ApiResponse(boolean success, String message, T data) {
    //    this.success = success;
    //    this.message = message;
    //    this.data = data;
    //}

    // 成功回應
    // 當方法是 static，而且要用泛型時，需要在方法前面宣告 <T>，因為static方法在「物件建立之前」就存在，看不到類別的泛型T，所以自己宣告
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "操作成功", data);
    }

    // 成功回應（自訂訊息）
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // 失敗回應
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}