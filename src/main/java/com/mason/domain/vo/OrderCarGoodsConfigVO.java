package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCarGoodsConfigVO {
    private Integer optionId;
    private Integer valueId;
    private String name;
}
