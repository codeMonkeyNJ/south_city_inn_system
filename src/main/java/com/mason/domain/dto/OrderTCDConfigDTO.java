package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderTCDConfigDTO {
    private Integer comboDetailId;
    private Integer valueId;// 配置项值id
    private String name;// 配置项值名称
}
