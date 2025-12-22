package com.purelife.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {

    @NotNull(message = "商品ID不能為空")
    private Integer productId;

    @Min(value = 1, message = "數量至少為1")
    private Integer quantity = 1;  // 預設為 1
}