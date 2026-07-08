package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptVO {
    private String name;
    private Integer type;
    private Integer fatherId;
    private String father;
    private Integer sort;
    private Boolean state;
    private String address;
    private String detail;
}
