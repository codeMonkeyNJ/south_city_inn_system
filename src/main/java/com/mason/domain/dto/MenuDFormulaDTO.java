package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDFormulaDTO {
    private Integer id;//原料id
    private Integer num;// 数量
    private String unit;// 单位
    private Integer step;// 步骤
    private String detail;// 详情操作
}
