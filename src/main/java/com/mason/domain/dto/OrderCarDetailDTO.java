package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCarDetailDTO {
    private Integer goodsType;
    private Integer goodsId;
    private List<OrderComboDetailDTO> comboDetails;
    private List<OrderCarGoodsConfigDTO> configs;
    private Integer num;
    private Integer couponId;//商品券或折扣券id
}
