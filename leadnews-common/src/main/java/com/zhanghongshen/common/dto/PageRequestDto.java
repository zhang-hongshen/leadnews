package com.zhanghongshen.common.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDto {

    protected Integer size;
    protected Integer page;

}
