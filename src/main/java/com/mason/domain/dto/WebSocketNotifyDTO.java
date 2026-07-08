package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketNotifyDTO {
    public static final int PURCHASE_APPLY = 0;
    private Integer orderId;
    private Integer Type;
}
