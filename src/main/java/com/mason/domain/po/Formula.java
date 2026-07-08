package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Formula {
    private Integer dishId;
    private Integer materialId;
    private Integer num;
    private String unit;
    private Integer step;
    private String detail;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}

