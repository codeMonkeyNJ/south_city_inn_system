package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseApplyRejectDTO {
    private Integer id;//采购申请单id
    private String cause;//驳回原因
}
