package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAuthSimDTO {
    private String code;//权限码
    private Integer dataCoverage;//权限名称
}
