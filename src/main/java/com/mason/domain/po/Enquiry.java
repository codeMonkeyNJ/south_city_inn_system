package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Enquiry {
    private Integer id;
    private Integer applicantId;
    private Integer deptId;
    private Integer storeId;
    private String no;
    private BigDecimal amount;
    private Integer payType;
    private Integer state;
    private String reqCause;
    private String respCause;
    private String attachment;
    private String remark;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
