package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreAmendDetail {
    private Integer amendId;
    private Integer repertoryId;
    private Integer num;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
