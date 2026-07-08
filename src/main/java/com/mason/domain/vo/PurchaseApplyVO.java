package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseApplyVO {
    private String no;
    private Integer deptId;
    private String dept;
    private Integer applicantId;
    private String applicant;
    private Integer storeId;
    private String store;
    private Integer state;
    private Integer purchaseId;
    private String remark;
    private String cause;
    private LocalDateTime applyTime;
    private List<PurchaseApplyDetailVO> detail;

}
