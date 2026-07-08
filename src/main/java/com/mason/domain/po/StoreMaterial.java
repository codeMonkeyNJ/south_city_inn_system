package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreMaterial {
    private Integer id;//库存id
    private Integer storeId;//仓库id
    private Integer materialId;//物料id
    private Integer sum;//库存数量
    private LocalDateTime updateTime;//修改时间
    private LocalDateTime createTime;//创建时间
}
