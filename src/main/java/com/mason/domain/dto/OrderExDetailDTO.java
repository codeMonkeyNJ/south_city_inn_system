package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderExDetailDTO {
    private Integer id;// 订单明细id
    private Integer orderId;// 订单id
    private Integer goodsType;// 商品类型(0:菜品,1:套餐)
    private Integer goodsId;// 商品id
    private List<OrderExComboDetailDTO> comboDetails;// 套餐明细
    private List<OrderExGoodsConfigDTO> configs;// 商品配置
    private Integer num;// 商品数量
    private Integer couponId;//商品券或折扣券id
    private BigDecimal finishPrice ;//明细最终价格
}
