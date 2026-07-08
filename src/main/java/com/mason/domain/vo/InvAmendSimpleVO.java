package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvAmendSimpleVO {
    private Integer id;//修正单id
    private String deptName;//申请部门/门店名称
    private String repertory;//申请人
    private Integer state;//状态
    private String createTime;//创建时间
}
