package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleSimpleVO {
    private Integer id; //角色id
    private String name;//角色名称
    private String desc;//角色描述

}
