package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketNotifyVO {
    public static final int PURCHASE_APPLY = 0;
    private Integer orderId;
    private Integer Type;
}
