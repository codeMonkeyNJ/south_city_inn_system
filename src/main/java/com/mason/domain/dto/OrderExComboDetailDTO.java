package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderExComboDetailDTO {
    private Integer id;//订单套餐明细id
    private Integer detailId;//明细id
    private Integer dishId;//菜品id
    private Integer num;//菜品数量
    private List<OrderExCDConfigDTO> configs;//菜品配置信息
}
