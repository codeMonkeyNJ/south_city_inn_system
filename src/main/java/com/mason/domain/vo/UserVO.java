package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private String nickname;//昵称
    private String username;//用户名
    private String phone;//手机号
    private String avatar;//头像
    private LocalDate birthday;//生日
    private Integer gender;// 性别
    private String address;// 地址
    private Boolean state;//状态
    private List<Integer> roleIds;//角色id列表
    private String role;//角色
    private List<Integer> deptIds;//部门id列表
    private String dept;// 部门
    private String name;// 姓名
    private String idCard;// 身份证
    private String bankName;// 银行名称
    private String bankCard;// 银行卡
    private Double salary;// 薪资
    private String remark;//备注
}
