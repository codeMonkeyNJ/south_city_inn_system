package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Integer id;
    private Integer type;
    private Integer deptId;
    private Integer state;
    private BigDecimal payMoney;
    private Integer couponId;
    private Integer pickupNum;
    private Integer eatMode;
    private Integer payMode;
    private LocalDateTime payTime;
    private LocalDateTime completeTime;
    private LocalDateTime tackTime;
    private String remark;
    private Integer activityId;
    private List<Integer> couponIds;
    private List<OrderDetailDTO> orderDetails;
}
