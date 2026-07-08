package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Purchase {
    private Integer id;// 采购单id
    private Integer applyId;// 申请单id
    private String no;// 采购单编号
    private Integer deptId;// 部门id
    private Integer buyerId;// 采购人id
    private Integer stockerId;// 入库人id
    private Integer storeId;// 入库仓库id
    private Integer state;// 采购单状态
    private Float money;// 采购金额
    private String remark;// 采购单备注
    private LocalDateTime updateTime;// 更新时间
    private LocalDateTime createTime;// 创建时间
}
