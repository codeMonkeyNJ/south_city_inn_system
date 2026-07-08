package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutletGoodsDTO {
    private Integer id;//门店id
    private Integer type;//0-菜品 1-套餐
    private Integer goodsId;//菜品id/套餐id
    private Boolean state;//true-正常 false-售罄
}
