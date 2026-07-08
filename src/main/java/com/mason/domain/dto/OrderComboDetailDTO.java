package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderComboDetailDTO {
    private Integer dishId;//菜品id
    private Integer num;//菜品数量
    private List<OrderGoodsConfigDTO> configs;//菜品配置信息
}
