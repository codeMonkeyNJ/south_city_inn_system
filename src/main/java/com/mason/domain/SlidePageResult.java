package com.mason.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlidePageResult<T> {
    private Integer lastId;
    private Boolean hasMore;
    private List<T> items;
}
