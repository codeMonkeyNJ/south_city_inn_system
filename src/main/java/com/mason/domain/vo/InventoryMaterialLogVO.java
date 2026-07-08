package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryMaterialLogVO {
    private Integer operation;//0入库 1出库
    private Integer num;// 数量
    private LocalDateTime operationTime;// 操作时间
    private Integer sourceType;// 来源类型
    private Integer sourceId;// 来源id
}
