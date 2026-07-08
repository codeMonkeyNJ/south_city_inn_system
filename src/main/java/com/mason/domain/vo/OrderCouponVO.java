package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCouponVO {
    private Integer id;// 优惠券id
    private String Name;// 优惠券名称
}
