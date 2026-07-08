package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdatePasswordDTO {
    private Integer id;// 用户id
    private String oldPassword;// 旧密码
    private String newPassword;// 新密码
}
