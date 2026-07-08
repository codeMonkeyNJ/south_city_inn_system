package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Auth {
    private Integer id;
    private String code;
    private String name;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
