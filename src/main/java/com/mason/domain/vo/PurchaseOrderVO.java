package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderVO {
    private String no;
    private Integer applyId;
    private String applyNo;
    private Integer deptId;
    private String dept;
    private Integer buyerId;
    private String buyer;
    private Integer stockerId;
    private String stocker;
    private Integer storeId;
    private String store;
    private Integer state;
    private Float money;
    private String remark;
    private List<PurchaseFullDetailVO> detail;
}
