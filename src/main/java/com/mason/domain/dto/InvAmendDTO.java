package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvAmendDTO {
    private Integer id;//库存修正单id
    private Integer state;//修正单状态(0待审核 1审核通过 2审核)
    private Integer storeId;//仓库id
    private String remark;//备注
    private String cause;//驳回原因
    private List<InvAmendDetailDTO> materials;//修正单明细
}
