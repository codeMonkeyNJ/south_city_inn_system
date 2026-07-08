package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseApplySimpleVO {
    private Integer id;//采购申请id
    private String no;//采购申请单号
    private String dept;//部门名称
    private String applicant;//申请人名称
    private Integer state;//状态
    private Integer purchaseId;//采购单id
    private LocalDateTime applyTime;//申请时间
}
