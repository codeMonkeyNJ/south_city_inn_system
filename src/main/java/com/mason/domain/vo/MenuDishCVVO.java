package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDishCVVO {
    private Integer valueId;
    private String name;
    private BigDecimal spread;
    private List<MenuDishCVClVO> changes;
}
