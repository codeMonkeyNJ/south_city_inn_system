package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCouRollbackDTO {
    private Integer clientId;//客户id
    private Integer couponId;//优惠券id
    private Integer num;// 数量
}
