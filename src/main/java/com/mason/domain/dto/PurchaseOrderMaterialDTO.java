package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderMaterialDTO {
    private Integer materialId;//采购物料id
    private Integer supplierId;//供应商id
    private Integer planNum;//计划采购数量
    private Integer realNum;//实际采购数量
    private Float money;//采购金额(总价)
    private String remark;//备注
}
