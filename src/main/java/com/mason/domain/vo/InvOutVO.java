package com.mason.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvOutVO {
    private String no;
    private Integer enquiryId;
    private String enquiryNo;
    private String stocker;
    private String storeOut;
    private String storeIn;
    private String dept;
    private Integer state;
    private String attachment;
    private List<InvOutDetailVO> details;
}
