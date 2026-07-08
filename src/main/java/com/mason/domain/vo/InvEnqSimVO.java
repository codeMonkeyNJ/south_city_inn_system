package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvEnqSimVO {
    private Integer id;
    private Integer outboundId;
    private String dept;
    private Integer amount;
    private Integer state;
}
