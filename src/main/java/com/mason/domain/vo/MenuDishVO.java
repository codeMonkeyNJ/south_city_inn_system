package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDishVO {
    private String cover;
    private String name;
    private String className;
    private String intro;
    private String introImage;
    private BigDecimal price;
    private Boolean state;
    private List<MenuDishFormulaVO> formula;
    private List<MenuDishConfigVO> configs;
}
