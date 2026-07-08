package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDishSimpleVO {
    private Integer id;
    private String cover;
    private String name;
    private String className;
    private BigDecimal price;
    private Boolean state;
}
