package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dept {
    private Integer id;//部门id
    private String name;//部门名称
    private Integer type;//部门类型
    private Integer fatherId;//父部门id
    private Integer state;//部门状态
    private String address;//部门地址
    private String detail;//部门描述
    private Integer sort;//部门排序
    private LocalDateTime updateTime;//更新时间
    private LocalDateTime createTime;//创建时间
}
