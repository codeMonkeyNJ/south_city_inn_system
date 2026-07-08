package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigValue {
    private Integer id;
    private Integer optionId;
    private String name;
    private BigDecimal spread;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
