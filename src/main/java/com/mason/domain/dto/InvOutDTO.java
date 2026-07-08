package com.mason.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvOutDTO {
    private Integer id;
    private Integer enquiryId;
    private Integer storeOutId;
    private String attachment;
    private List<InvOutDetailDTO> details;
}
