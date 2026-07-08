package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseApplyDetailVO {
    private Integer materialId;//采购明细的原料id
    private String name;//采购明细的原料名称
    private Integer planNum;//计划采购数量
    private String unit;// 单位
}
