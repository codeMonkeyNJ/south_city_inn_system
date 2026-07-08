package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Material {
    private Integer id;//原料id
    private String cover;//原料封面
    private String name;//原料名称
    private Integer classId;//原料分类id
    private Integer num;//计量数值
    private String unit;//计量单位
    private String pack;//包装单位
    private Float price;//原料单价
    private Integer state;//原料状态（0为正常，1为售罄，2为弃用）
    private String remark;//原料备注
    private LocalDateTime updateTime;//更新时间
    private LocalDateTime createTime;//创建时间

}
