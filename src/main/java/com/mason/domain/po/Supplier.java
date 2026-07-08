package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Supplier {
    private Integer id;//供应商id
    private String name;//供应商名称
    private String address;//供应商地址
    private String phone;//供应商电话
    private String linkName;//供应商联系人
    private String accountName;//供应商账户名称
    private String bank;//供应商开户银行
    private String account;//供应商银行账号
    private LocalDateTime updateTime;//供应商更新时间
    private LocalDateTime createTime;//供应商创建时间
}
