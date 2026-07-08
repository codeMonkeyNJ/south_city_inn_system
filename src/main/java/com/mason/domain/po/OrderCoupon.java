package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCoupon {
    private Integer id;
    private Integer couponId;
    private Integer orderId;
    private LocalDateTime updatedTime;
    private LocalDateTime createdTime;
}
