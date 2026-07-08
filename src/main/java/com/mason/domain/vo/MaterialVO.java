package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialVO {
    private String cover;//原料封面
    private String name;//原料名称
    private Integer classId;//原料分类名称
    private String className;//原料分类名称
    private Integer num;//计量数值
    private String unit;//计量单位
    private String pack;//包装单位
    private Float price;//原料单价
    private Integer state;//原料状态（0为正常，1为售罄，2为弃用）
    private String remark;//原料备注
}
