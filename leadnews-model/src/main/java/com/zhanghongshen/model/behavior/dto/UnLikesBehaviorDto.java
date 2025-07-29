package com.zhanghongshen.model.behavior.dto;

import com.zhanghongshen.common.annotation.StringNumberAdapter;
import lombok.Data;

@Data
public class UnLikesBehaviorDto {
    // 文章ID
    @StringNumberAdapter
    Long articleId;

    /**
     * 不喜欢操作方式
     * 0 不喜欢
     * 1 取消不喜欢
     */
    Short type;

}