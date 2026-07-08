package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryEnquiryPayDTO {
    private Integer id;// 要货单id
    private Integer payMode;// 付款方式
}
