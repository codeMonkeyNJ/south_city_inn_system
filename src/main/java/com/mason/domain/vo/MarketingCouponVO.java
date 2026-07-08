package com.mason.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketingCouponVO {
    private String name;
    private String cover;
    private Integer type;
    private String rule;
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
    private List<MarketingCDeptVO> depts;
    private List<MarketingCDishVO> dishes;
    private List<MarketingCComboVO> combos;
}
