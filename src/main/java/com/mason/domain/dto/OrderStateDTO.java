package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStateDTO {
    private Integer id;
    private Integer state;
    private String cause;//订单退款原因,state=5时填写
}
