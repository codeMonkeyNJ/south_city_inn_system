package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAuth {
    private Integer roleId;
    private Integer authId;
    private Integer dataCoverage;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
