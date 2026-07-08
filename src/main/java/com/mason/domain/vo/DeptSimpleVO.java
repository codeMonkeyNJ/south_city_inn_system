package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptSimpleVO {
    private Integer id;//部门id
    private String name;//部门名称
    private Integer type;//部门类型
    private Integer fatherId;//父部门id
    private Boolean state;//状态
    private String detail;//部门描述
}
