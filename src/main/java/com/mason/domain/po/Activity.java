package com.mason.domain.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Activity implements Serializable {
    private Integer id;
    private String name;
    private Integer ruleId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String cover;
    private String detailsImage;
    private Integer type;
    private Integer goodsId;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}

