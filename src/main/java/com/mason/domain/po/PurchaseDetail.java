package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDetail {
    private Integer id;//采购明细id
    private Integer sourceType;//采购明细来源类型
    private Integer sourceId;//采购明细来源id
    private Integer materialId;//物料id
    private Integer supplierId;//供应商id
    private Integer planNum;//计划数量
    private Integer realNum;//实际数量
    private Float money;//采购金额
    private LocalDateTime expirationDate;//原料过期日期（有效期）
    private String remark;//采购明细备注
    private LocalDateTime updateTime;//修改时间
    private LocalDateTime createTime;//创建时间

}
