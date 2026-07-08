package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialCarItemVO {
    private Integer materialId;
    private String name;
    private Integer num;
    private Float money;
}
