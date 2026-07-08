package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderSimpleVO {
    private Integer id;// 采购订单id
    private String no;// 采购订单编号
    private String dept;// 采购部门
    private String buyer;// 采购订单采购员
    private String stocker;//入库员
    private String store;//入库仓库
    private Integer state;//采购订单状态
}
