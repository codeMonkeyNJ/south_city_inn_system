package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryStoreSimpleVO {
    private Integer id;//仓库id
    private Integer deptId;//部门id
    private String name;//仓库名称
    private String address;//仓库地址
}
