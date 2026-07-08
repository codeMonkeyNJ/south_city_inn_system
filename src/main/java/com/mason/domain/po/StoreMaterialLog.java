package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreMaterialLog {
    private Integer id;// 库存日志id
    private Integer repertoryId;// 库存id
    private Integer operation;// 操作类型(0为出库,1为入库)
    private Integer num;// 数量
    private Integer sourceType;// 来源类型(0为采购订单,1为出库单,2为商品订单)
    private Integer sourceId;// 来源id
    private LocalDateTime updateTime;// 更新时间
    private LocalDateTime createTime;// 创建时间
}
