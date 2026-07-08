package com.mason.domain.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    private Integer id;
    private String name;
    private String cover;
    private String rule;
    private Integer type;
    private BigDecimal discount;
    private BigDecimal derate;
    private BigDecimal threshold;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime enableTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime disableTime;
    private Boolean state;
    private Integer limit;
    private Boolean overlay;
    private Integer sum;
    private Integer stock;
    private Integer usage;
    private BigDecimal price;
    private Boolean allDepts;
    private Boolean allDishes;
    private Boolean allCombos;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
