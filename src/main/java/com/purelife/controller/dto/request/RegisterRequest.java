//前端傳來的註冊資料會包裝成這個物件，並自動驗證格式是否正確。
package com.purelife.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

//@Valid + Bean Validation（@NotBlank、@Size、@Email、@Pattern）的機制，Spring 已經自動做了
//因此這裡驗證失敗不用手動拋出例外，它會自動拋出 MethodArgumentNotValidException。
@Data
public class RegisterRequest {
    
    @NotBlank(message = "帳號不能為空")//驗證規則，當 Controller 加上 @Valid 時，這些驗證規則才會被執行！
    @Size(min = 4, max = 20, message = "帳號長度必須在 4-20 字元之間")
    private String account;
    
    @NotBlank(message = "密碼不能為空")
    @Size(min = 8, message = "密碼長度至少 8 字元")
    private String password;
    
    @NotBlank(message = "姓名不能為空")
    private String name;
    
    @NotBlank(message = "Email 不能為空")
    @Email(message = "Email 格式不正確")
    private String email;
    
    @NotBlank(message = "手機號碼不能為空")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    private String phone;
}
