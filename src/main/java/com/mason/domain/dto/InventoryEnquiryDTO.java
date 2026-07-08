package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryEnquiryDTO {
    private Integer deptId;
    private Integer storeId;
    private String remark;
    private Integer payType;
    private List<InventoryEnquiryDetailDTO> details;
}
