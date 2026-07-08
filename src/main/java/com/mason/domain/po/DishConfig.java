package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishConfig {
    private Integer dishId;
    private Integer optionId;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
