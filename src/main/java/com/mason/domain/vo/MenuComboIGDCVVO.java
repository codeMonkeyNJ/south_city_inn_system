package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuComboIGDCVVO {
    private Integer valueId;
    private String name;
    private BigDecimal spread;
}
