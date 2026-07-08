package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDishDTO {
    private Integer id;
    private String cover;
    private String name;
    private List<Integer> classIds;
    private BigDecimal price;
    private String intro;
    private String introImage;
    private Boolean state;
    private Integer sort;
    private List<MenuDFormulaDTO> formula;
    private List<MenuDComfigDTO> configs;
}
