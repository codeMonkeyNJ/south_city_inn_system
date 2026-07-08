package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuComboGDTO {
    private Integer groupId;//套餐分组id
    private Integer num;//可选数量
}
