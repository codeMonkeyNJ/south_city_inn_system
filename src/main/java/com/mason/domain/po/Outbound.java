package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Outbound {
    private Integer id;
    private Integer enquiryId;
    private String no;
    private Integer stockerId;
    private Integer storeOutId;
    private Integer storeInId;
    private Integer state;
    private String attachment;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;

}
