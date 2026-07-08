package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderComboDetail {
    private Integer orderDetailId;//订单明细id
    private Integer dishId;//菜品id
    private Integer num;//菜品数量
    private LocalDateTime updateTime;//修改时间
    private LocalDateTime createTime;//创建时间
}
