package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSimpleVO {
    private Integer id;                 // 用户id
    private String nickname;            // 昵称
    private String username;            // 用户名
    private String avatar;              // 头像
    private Boolean state;              // 账户状态
    private String role;                // 角色
    private String dept;                // 部门
    private LocalDateTime updateTime;   // 更新时间
    private LocalDateTime createTime;   // 创建时间
}
