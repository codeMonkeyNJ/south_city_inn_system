package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvOutDetailDTO {
    private Integer materialId;
    private Integer num;
    private String remark;
}
