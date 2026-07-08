package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryMaterialVO {
    private Integer id;//库存id
    private Integer materialId;//物料id
    private String name;//物料名称
    private String className;//物料类别名称
    private Integer sum;//库存数量
}
