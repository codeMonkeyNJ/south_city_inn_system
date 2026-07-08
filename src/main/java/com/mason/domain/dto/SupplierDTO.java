package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDTO {
    private Integer id;//供应商id
    private String name;//供应商名称
    private String address;//供应商地址
    private String phone;//供应商电话
    private String linkName;//供应商联系人
    private String accountName;//供应商账户名称
    private String bank;//供应商开户银行
    private String account;//供应商银行账号
    private List<Integer> materials;//供应商提供的物料id
}
