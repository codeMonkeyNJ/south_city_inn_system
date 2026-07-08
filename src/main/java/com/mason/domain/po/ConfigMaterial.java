package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigMaterial {
    private Integer dishId;
    private Integer optionId;
    private Integer valueId;
    private Integer materialId;
    private Integer spread;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
