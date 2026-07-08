package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketingCSimDTO {
    private Integer id;//优惠券id
    private String cover;//优惠券图片
    private String name;//优惠券名称
}
