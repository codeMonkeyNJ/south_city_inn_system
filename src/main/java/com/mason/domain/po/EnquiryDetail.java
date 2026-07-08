package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnquiryDetail {
    private Integer id;
    private Integer sourceType;
    private Integer sourceId;
    private Integer materialId;
    private Integer num;
    private BigDecimal money;
    private String remark;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
