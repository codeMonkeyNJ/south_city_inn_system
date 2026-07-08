package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private Integer id;//角色id
    private String name;//角色名称
    private String desc;//角色描述
    private List<RoleAuthDTO> permissions;//权限列表
}
