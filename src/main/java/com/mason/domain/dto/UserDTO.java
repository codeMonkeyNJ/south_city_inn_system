package com.mason.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Integer id; //用户id
    private String username;// 用户名
    private String nickname;// 昵称
    private String phone;// 手机号
    private Integer gender;// 性别
    private String address;// 地址
    private String avatar;// 头像
    private String name;// 姓名
    private String idCard;// 身份证
    private String bankName;// 银行名称
    private String bankCard;// 银行卡
    private Double salary;// 薪资
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;// 生日
    private List<Integer> roleIds;// 角色id列表
    private List<Integer> deptIds;// 部门id列表
    private String remark;// 备注
    private Boolean state;// 状态
}
