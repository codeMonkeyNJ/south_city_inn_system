package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvEnqVO {
    private Integer outboundId;
    private Integer applicantId;
    private String applicant;
    private Integer deptId;
    private String dept;
    private Integer storeId;
    private String store;
    private String no;
    private BigDecimal amount;
    private Integer state;
    private String remark;
    private String reqCause;
    private String respCause;
    private String attachment;
    private List<InvEnqDetailVO> details;
}
