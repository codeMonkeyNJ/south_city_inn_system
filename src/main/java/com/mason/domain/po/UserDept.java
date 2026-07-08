package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDept {
    private Integer userId;
    private Integer deptId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
