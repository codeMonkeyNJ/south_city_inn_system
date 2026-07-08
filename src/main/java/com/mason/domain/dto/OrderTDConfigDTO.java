package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderTDConfigDTO {
    private Integer detailId;// 明细id
    private String name;// 配置项值名称
    private Integer valueId;// 配置项值id
}
