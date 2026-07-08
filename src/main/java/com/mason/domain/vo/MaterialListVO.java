package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialListVO {
    private Integer id;//原料id
    private String cover;//原料封面
    private String name;//原料名称
    private String className;//原料分类名称
    private Integer num;//原料数量
    private String unit;//原料单位
    private String pack;//原料包装
    private Float price;//原料价格
    private Integer state;//状态

}
