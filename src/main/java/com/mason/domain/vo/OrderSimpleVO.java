package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSimpleVO {
    private Integer id;
    private Integer type;
    private Integer state;
    private Integer eatMode;
    private Integer deptId;
    private String deptName;
    private BigDecimal finishPrice;
    private LocalDateTime expirationTime;
    private LocalDateTime createTime;
    private List<OrderDetailVO> orderDetails;
}
