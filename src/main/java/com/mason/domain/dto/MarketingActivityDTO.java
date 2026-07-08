package com.mason.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketingActivityDTO implements Serializable {
    private Integer id;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private Boolean vip;
    private String cover;
    private String detailsImage;
    private Integer routeType;
    private Integer activityType;
    private Integer goodsId;
    private Boolean once;
    private Boolean allDepts;
    private Boolean allDishes;
    private Boolean allCombos;
    private List<Integer> depts;
    private List<Integer> dishes;
    private List<Integer> combos;
    private Boolean overlay;
    private Integer buyNum;
    private Integer giveNum;
    private BigDecimal discount;
    private Integer awardGoodsType;
    private Integer awardGoodsId;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
}

