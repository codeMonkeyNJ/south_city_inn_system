package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvOutSimVO {
    private Integer id;
    private String no;
    private String stocker;
    private String store;
    private String dept;
    private Integer state;
    private LocalDateTime updateTime;
}
