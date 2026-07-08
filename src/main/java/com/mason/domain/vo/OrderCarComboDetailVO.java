package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCarComboDetailVO {
    private Integer dishId;//菜品id
    private String dishName;//菜品名称
    private Integer num;//菜品数量
    private List<OrderCarGoodsConfigVO> configs;//菜品配置信息
}
