package com.mason.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialClass {
    private Integer id;//原料分类id
    private String name;//原料分类名称
    private LocalDateTime updateTime;//更新时间
    private LocalDateTime createTime;//创建时间
}
