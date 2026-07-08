package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvLogDTO {
    private Integer id;//库存日志id
    private Integer repertoryId;//库存id
    private Integer storeId;//仓库id
    private Integer materialId;//原料id
    private Integer operation;//操作类型
    private Integer num;// 数量
    private Integer sourceId;//来源id
    private Integer sourceType;//来源类型
    private String createTime;//创建时间
}
