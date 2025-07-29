package com.zhanghongshen.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseResult<T> extends ResponseResult<T> {
    private Integer currentPage;
    private Integer size;
    private Integer total;
}
