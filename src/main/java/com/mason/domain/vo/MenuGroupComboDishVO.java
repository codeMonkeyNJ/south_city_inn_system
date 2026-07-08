package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuGroupComboDishVO {
    private Integer groupId;//分组id
    private List<Integer> dishIds;//菜品id列表
    private List<Integer> comboIds;//套餐id列表
}
