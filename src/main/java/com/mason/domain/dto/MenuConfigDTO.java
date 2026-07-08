package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuConfigDTO {
    private Integer id;
    private String name;
    private Integer sort;
    private List<MenuConfigValueDTO> values;
}
