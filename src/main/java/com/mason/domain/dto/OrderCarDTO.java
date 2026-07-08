package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCarDTO {
    private Integer type;//订单类型(0为点餐订单，1为购买优惠券订单，2为购买VIP订单)
    private Integer deptId;//门店id
    private Integer eatMode;//用餐类型
    private String remark;//备注
    private Integer activityId;//参与的活动id
    private List<Integer> couponIds;//使用的优惠券id列表
    private List<OrderCarDetailDTO > orderDetails;//购物车详情
    private BigDecimal amount;//订单总金额
}
