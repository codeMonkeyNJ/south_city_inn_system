package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private Integer id;
    private String username;
    private String password;
    private String phone;
    private Integer gender;
    private Integer points;
    private Boolean vip;
    private LocalDate updateTime;
    private LocalDate createTime;
}
