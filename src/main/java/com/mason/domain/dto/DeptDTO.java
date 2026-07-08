package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptDTO {
    private Integer id;//部门id
    private String name;//部门名称
    private Integer fatherId;//父部门id
    private Integer type;//部门类型 0表示总部部门，1表示门店部门
    private Boolean state;//部门状态
    private Integer sort;//部门排序
    private String address;//部门地址
    private String detail;//部门描述
}
