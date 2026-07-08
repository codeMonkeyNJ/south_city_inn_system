package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCarComboDetailDTO {
    private Integer id;//菜品id
    private Integer num;//菜品数量
    private List<OrderCarGoodsConfigDTO> configs;//菜品配置信息
}
