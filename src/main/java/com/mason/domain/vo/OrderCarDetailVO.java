package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCarDetailVO {
    private Integer goodsType;//商品类型(0:菜品,1:套餐,2:优惠券)
    private Integer goodsId;//商品id
    private String goodsName;//商品名称
    private String cover;//商品图片
    private List<OrderCarComboDetailVO> comboDetails;//套餐明细
    private List<OrderCarGoodsConfigVO> configs;//菜品配置
    private Integer num;//商品数量
    private Integer couponId;//商品券或折扣券id
    private BigDecimal originalPrice;//明细原价
    private BigDecimal finishPrice ;//明细最终价格
}
