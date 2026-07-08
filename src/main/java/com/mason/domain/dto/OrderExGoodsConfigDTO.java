package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderExGoodsConfigDTO {
    private Integer detailId;// 明细id
    private Integer dishId;// 菜品id
    private Integer valueId;// 配置项值id
}
