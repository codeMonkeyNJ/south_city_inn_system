package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVO {
    private Integer id;// 订单明细id
    private Integer goodsType;// 商品类型(0:菜品,1:套餐)
    private Integer goodsId;// 商品id
    private String goodsName;// 商品名称
    private String cover;// 商品图片
    private List<OrderComboDetailVO> comboDetails;// 套餐明细
    private List<OrderGoodsConfigVO> configs;// 商品配置
    private Integer num;// 商品数量
    private Integer couponId;//商品券或折扣券id
    private BigDecimal payMoney ;//明细最终价格
}
