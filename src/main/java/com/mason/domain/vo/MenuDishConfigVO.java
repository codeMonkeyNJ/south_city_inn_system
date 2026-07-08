package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDishConfigVO {
    private Integer optionId;
    private String name;
    private List<MenuDishCVVO> values;
}
