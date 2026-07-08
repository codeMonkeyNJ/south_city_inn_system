package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvAmendDetailDTO {
    private Integer repertoryId;//库存ID
    private Integer num;// 数量
}
