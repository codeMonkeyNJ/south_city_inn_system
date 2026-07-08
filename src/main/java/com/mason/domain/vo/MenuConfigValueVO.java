package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuConfigValueVO {
    private Integer id;
    private String name;
    private BigDecimal spread;
}
