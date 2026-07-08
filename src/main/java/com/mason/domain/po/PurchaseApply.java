package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseApply {
    private Integer id;//采购申请申请id
    private String no;//采购申请单号
    private Integer applicantId;//申请人id
    private Integer deptId;//部门id
    private Integer storeId;//仓库id
    private Integer state;//状态
    private String remark;//备注
    private String cause;//驳回原因
    private LocalDateTime updateTime;//修改时间
    private LocalDateTime createTime;//创建时间
}
