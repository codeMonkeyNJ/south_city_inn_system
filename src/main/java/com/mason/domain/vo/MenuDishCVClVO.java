package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDishCVClVO {
    private Integer materialId;
    private String name;
    private Integer spread;
    private String unit;
}
