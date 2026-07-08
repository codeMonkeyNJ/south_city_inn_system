package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsClass {
    private Integer id;
    private Integer goodsType;
    private Integer dishId;
    private Integer comboId;
    private Integer menuClassId;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
