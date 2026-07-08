package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuComboIGDCVDTO {
    private Integer dishId;//菜品id
    private Integer optionId;//配置项id
    private Integer valueId;//配置值id
    private String name;//配置值名称
    private BigDecimal spread;//配置值对应差价
}
