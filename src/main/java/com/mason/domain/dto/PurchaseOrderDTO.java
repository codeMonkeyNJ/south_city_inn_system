package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderDTO {
    private Integer id;//采购单id
    private Integer applyId;//采购申请单id
    private Integer state;//状态
    private Integer deptId;//创建部门id
    private Integer stockerId;//入库员id
    private Integer storeId;//仓库id
    private Float money;//采购金额
    private String remark;//备注
    private List<PurchaseOrderMaterialDTO> detail;//采购单的采购明细
}
