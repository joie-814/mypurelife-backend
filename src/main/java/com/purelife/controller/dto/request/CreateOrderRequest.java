package com.purelife.controller.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotBlank(message = "收件人姓名不能為空")
    private String recipientName;

    @NotBlank(message = "收件人電話不能為空")
    @Pattern(regexp = "^09\\d{8}$", message = "請輸入正確的手機號碼格式")
    private String recipientPhone;

    @NotBlank(message = "收件地址不能為空")
    private String recipientAddress;

    @NotBlank(message = "請選擇付款方式")
    private String paymentMethod;  // credit_card, atm, cvs (超商代碼)
}
