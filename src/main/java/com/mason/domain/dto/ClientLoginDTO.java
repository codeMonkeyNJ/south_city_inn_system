package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientLoginDTO {
    private Integer mode;
    private String username;
    private String password;
    private String phone;
    private String code;
}
