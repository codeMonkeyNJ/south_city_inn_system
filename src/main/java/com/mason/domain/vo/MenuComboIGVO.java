package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuComboIGVO {
    private Integer groupId;
    private String name;
    private Integer num;
    private List<MenuComboIGDVO> dishes;

}
