package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseSupplierSimpleVO {
    private Integer id;//供应商id
    private String name;//供应商名称
    private String address;//供应商地址
    private String phone;//供应商电话
}
