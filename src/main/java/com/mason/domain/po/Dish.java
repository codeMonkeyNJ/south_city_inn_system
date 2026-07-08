package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dish {
    private Integer id;
    private String name;
    private BigDecimal price;
    private Boolean state;
    private String intro;
    private String introImage;
    private String cover;
    private Integer sort;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
