package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuComboDTO {
    private Integer id;
    private String name;
    private String cover;
    private BigDecimal reducePrice;
    private String intro;
    private Boolean state;
    private Integer sort;
    private List<Integer> classIds;
    private List<MenuComboGDTO> groups;
}
