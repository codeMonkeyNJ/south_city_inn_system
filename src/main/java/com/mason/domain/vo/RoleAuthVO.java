package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAuthVO {
    private Integer id;//权限id
    private String name;//权限名称
    private String code;//权限编码
    private Integer dataCoverage;//数据范围
}
