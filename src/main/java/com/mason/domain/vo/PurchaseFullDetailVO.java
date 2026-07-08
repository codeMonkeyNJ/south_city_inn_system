package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseFullDetailVO {
    private Integer id;
    private String name;
    private Integer supplierId;
    private String supplier;
    private Integer planNum;
    private Integer realNum;
    private String unit;
    private Float money;
    private String remark;
}
