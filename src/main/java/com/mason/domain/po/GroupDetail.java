package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDetail {
    private Integer id;
    private Integer groupId;
    private Integer dishId;
    private Integer num;
    private Boolean required;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
