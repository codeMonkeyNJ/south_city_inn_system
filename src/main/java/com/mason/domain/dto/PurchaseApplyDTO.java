package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseApplyDTO {
    private Integer id;//采购单id
    private Integer deptId;//部门id
    private Integer storeId;//仓库id
    private Integer state;//状态
    private String cause;//驳回原因
    private String remark;//备注
    private List<PurchaseApplyMaterialDTO> detail;//采购明细
}
