package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvEnqDetailVO {
    private Integer id;
    private String name;
    private Integer num;
    private BigDecimal money;
    private String remark;
    private String unit;
}
