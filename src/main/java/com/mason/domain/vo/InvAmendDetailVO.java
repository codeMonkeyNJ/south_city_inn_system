package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvAmendDetailVO {
    private Integer repertoryId;// 库存id
    private String materialName;// 物料名称
    private Integer num;// 数量
}
