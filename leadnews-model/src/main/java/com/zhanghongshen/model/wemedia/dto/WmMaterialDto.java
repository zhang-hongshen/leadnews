package com.zhanghongshen.model.wemedia.dto;

import com.zhanghongshen.common.dto.PageRequestDto;
import lombok.Data;

@Data
public class WmMaterialDto extends PageRequestDto {

    /**
     * 1 收藏
     * 0 未收藏
     */
    private Short isCollection;
}
