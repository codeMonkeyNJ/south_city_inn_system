package com.mason.domain.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private Integer userId;//用户id
    private String name;// 姓名
    private Integer gender;// 性别
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;// 生日
    private String idCard;// 身份证
    private String address;// 地址
    private String bankName;// 银行名称
    private String bankCard;// 银行卡
    private Double salary;// 薪资
    private String phone;// 手机号
    private String remark;// 备注
    private LocalDateTime updateTime;// 更新时间
    private LocalDateTime createTime;// 创建时间
}
