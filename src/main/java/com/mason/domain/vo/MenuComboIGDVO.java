package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuComboIGDVO {
    private Integer dishId;
    private String name;
    private Integer num;
    private Boolean required;
    private BigDecimal price;
    private List<MenuComboIGDCVO> configs;
}
