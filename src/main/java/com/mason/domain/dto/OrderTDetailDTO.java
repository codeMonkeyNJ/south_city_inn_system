package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderTDetailDTO {
    private Integer orderId;// 订单id
    private Integer detailId;// 订单明细id
    private Integer goodsType;// 商品类型(0:菜品,1:套餐)
    private Integer goodsId;// 商品id
    private String goodsName;// 商品名称
    private String cover;// 商品图片
    private Integer num;// 商品数量
    private Integer couponId;//商品券或折扣券id
    private BigDecimal payMoney ;//明细最终价格
}
