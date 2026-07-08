package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventorEnquiryUpdateDTO {
    private Integer id;//要货单id
    private Integer state;//状态
    private Integer handle;// 处理方式(0：拒绝退款，1：同意退款，2：要求补充信息)
    private String reqCause;//请求取消/退款原因
    private String respCause;//回复取消/退款原因
    private String attachment;//附件
}
