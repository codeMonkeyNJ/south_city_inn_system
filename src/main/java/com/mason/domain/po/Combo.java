package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Combo {
    private Integer id;
    private String name;
    private String cover;
    private BigDecimal defPrice;
    private BigDecimal reducePrice;
    private String intro;
    private Boolean state;
    private Integer sort;
    private String updateTime;
    private String createTime;
}
