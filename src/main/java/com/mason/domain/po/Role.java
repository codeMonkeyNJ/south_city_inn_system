package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    private Integer id; // 角色id
    private String name;// 角色名称
    private String desc;// 角色描述
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
