package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String nickname;
    private String username;
    private String password;
    private String avatar;
    private Boolean state;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
