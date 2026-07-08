package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseApplyMaterialDTO {
    private Integer materialId;//采购物料id
    private Integer planNum;//计划采购数量
}
