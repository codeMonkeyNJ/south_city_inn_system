package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuComboGDVO {
    private Integer id;
    private String name;
    private Integer num;
    private Boolean required;
}
