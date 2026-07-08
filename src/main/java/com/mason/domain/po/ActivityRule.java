package com.mason.domain.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ActivityRule implements Serializable {
    private Integer id;
    private Boolean allDepts;
    private Boolean allDishes;
    private Boolean allCombos;
    private Boolean vip;
    private Boolean overlay;
    private Boolean once;
    private Integer type;
    private Integer buyNum;
    private Integer giveNum;
    private BigDecimal discount;
    private Integer goodsType;
    private Integer goodsId;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
