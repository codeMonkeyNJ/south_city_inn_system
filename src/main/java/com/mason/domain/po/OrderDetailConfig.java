package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailConfig {
    private Integer detailId;//订单明细id
    private Integer dishId;//菜品id
    private Integer valueId;//配置值id
    private LocalDateTime updateTime;//修改时间
    private LocalDateTime createTime;//创建时间
}
