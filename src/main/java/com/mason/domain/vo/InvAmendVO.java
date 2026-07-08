package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvAmendVO {
    private Integer deptId;
    private String deptName;
    private Integer storeId;
    private String storeName;
    private Integer applicantId;
    private String applicant;
    private Integer auditorId;
    private String auditor;
    private Integer state;
    private String remark;
    private String cause;
    private LocalDateTime createTime;
    private List<InvAmendDetailVO> details;
}
