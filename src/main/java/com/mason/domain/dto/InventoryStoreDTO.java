package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryStoreDTO {
    private Integer id; //仓库id
    private Integer deptId;//部门id
    private String name;//仓库名称
    private String address;//仓库地址
}
