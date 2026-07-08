package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuComboIGDCDTO {
    private Integer dishId;
    private Integer optionId;
    private String name;
}
