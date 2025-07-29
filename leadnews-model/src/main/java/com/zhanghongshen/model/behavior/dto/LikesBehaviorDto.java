package com.zhanghongshen.model.behavior.dto;

import lombok.Data;

@Data
public class LikesBehaviorDto {


    // 文章、动态、评论等ID
    Long articleId;
    /**
     * 喜欢内容类型
     * 0文章
     * 1动态
     * 2评论
     */
    Short type;

    /**
     * 喜欢操作方式
     * 0 点赞
     * 1 取消点赞
     */
    Short operation;


    public boolean checkParam() {
        return !(type > 2 || type < 0 || operation > 1 || operation < 0);
    }

}
