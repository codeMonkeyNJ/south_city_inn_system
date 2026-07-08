package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderTCDetailDTO {
    private Integer id;//唯一id
    private Integer detailID;//明细id
    private Integer dishId;//菜品id
    private String dishName;//菜品名称
    private Integer num;//菜品数量
}
