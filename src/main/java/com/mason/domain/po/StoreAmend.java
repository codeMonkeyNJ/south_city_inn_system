package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreAmend {
    private Integer id;
    private Integer storeId;
    private Integer state;
    private String remark;
    private String cause;
    private Integer applicantId;
    private Integer auditorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
