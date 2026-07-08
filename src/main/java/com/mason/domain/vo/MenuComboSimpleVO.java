package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuComboSimpleVO {
    private Integer id;
    private String name;
    private String cover;
    private String className;
    private Boolean state;
    private BigDecimal defPrice;
    private BigDecimal reducePrice;
}
