package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单详情实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {
    private Integer id;//明细id
    private Integer orderId;//订单id
    private Integer goodsId;//商品id
    private Integer goodsType;//商品类型
    private Integer num;//商品数量
    private Integer couponId;//明细使用的优惠券id
    private BigDecimal payMoney;//明细应付金额
    private LocalDateTime updateTime;//修改时间
    private LocalDateTime createTime;//创建时间
}
