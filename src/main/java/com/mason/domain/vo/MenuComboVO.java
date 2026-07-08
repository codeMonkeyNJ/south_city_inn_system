package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuComboVO {
    private String name;
    private String cover;
    private String intro;
    private BigDecimal defPrice;
    private BigDecimal reducePrice;
    private Boolean state;
    private List<MenuComboIGVO> groups;
}
