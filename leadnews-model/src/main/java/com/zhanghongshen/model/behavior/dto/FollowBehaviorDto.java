package com.zhanghongshen.model.behavior.dto;

import lombok.Data;

@Data
public class FollowBehaviorDto {
    //文章id
    Long articleId;
    //关注的id
    Long followId;
    //用户id
    Long userId;
}