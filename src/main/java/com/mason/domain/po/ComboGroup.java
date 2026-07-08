package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComboGroup {
    private Integer comboId;//套餐id
    private Integer groupId;//分组id
    private Integer num;//可选数量
    private LocalDateTime updateTime;//修改时间
    private LocalDateTime createTime;//创建时间
}
