package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptGoods {
    private Integer deptId;
    private Integer goodsType;
    private Integer goodsId;
    private Boolean state;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
