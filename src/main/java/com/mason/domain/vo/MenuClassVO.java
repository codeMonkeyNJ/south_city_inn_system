package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuClassVO {
    private Integer id;
    private String name;
    private Integer sort;
    private Boolean state;
}
