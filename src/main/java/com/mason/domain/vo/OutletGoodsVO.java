package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutletGoodsVO {
    private Integer id; // 商品id
    private Integer type;// 商品类型
    private String cover;// 封面图片
    private String name;// 商品名称
    private String className;// 分类名称
    private Boolean state;// 状态
    private BigDecimal price;// 售价
}
