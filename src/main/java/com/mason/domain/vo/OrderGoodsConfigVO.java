package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderGoodsConfigVO {
    private Integer valueId;// 配置项值id
    private String name;// 配置项值名称
}
