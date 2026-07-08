package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRefundDTO {
    private Integer id;//订单id
    private Boolean makeStart;//标记退款时是否已开始制作
}
