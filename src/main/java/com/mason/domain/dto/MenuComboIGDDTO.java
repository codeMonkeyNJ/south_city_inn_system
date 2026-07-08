package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuComboIGDDTO {
    private Integer groupId;
    private Integer dishId;
    private String name;
    private Integer num;
    private Boolean required;
    private BigDecimal price;
}
