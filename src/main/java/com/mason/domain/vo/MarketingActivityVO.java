package com.mason.domain.vo;

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
public class MarketingActivityVO implements Serializable {
    private String name;
    private String cover;
    private String detailsImage;
    private Integer routeType;
    private Integer goodsId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private Boolean vip;
    private Boolean overlay;
    private Boolean once;
    private Integer activityType;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    private Integer buyNum;
    private Integer giveNum;
    private BigDecimal discount;
    private Integer awardGoodsType;
    private Integer awardGoodsId;
    private Boolean allDepts;
    private Boolean allDishes;
    private Boolean allCombos;
    private List<DeptItem> depts;
    private List<DishItem> dishes;
    private List<ComboItem> combos;
    @Data
    public static class DeptItem implements Serializable {
        private Integer id;
        private String name;
    }
    @Data
    public static class DishItem implements Serializable {
        private Integer id;
        private String name;
    }

    @Data
    public static class ComboItem implements Serializable {
        private Integer id;
        private String name;
    }


}
