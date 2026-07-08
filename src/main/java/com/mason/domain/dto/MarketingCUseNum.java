package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketingCUseNum {
    private Integer couponId;//优惠券id
    private Integer useNum;//使用数量
}
