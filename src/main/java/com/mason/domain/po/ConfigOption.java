package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigOption {
    private Integer id;
    private String name;
    private Integer sort;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
