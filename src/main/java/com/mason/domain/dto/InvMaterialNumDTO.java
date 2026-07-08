package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvMaterialNumDTO {
    private Integer storeId;
    private Integer materialId;
    private Integer num;
}
