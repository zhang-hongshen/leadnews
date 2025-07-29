package com.zhanghongshen.behavior.service;

import com.zhanghongshen.model.behavior.dto.LikesBehaviorDto;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.behavior.dto.UnLikesBehaviorDto;

public interface LikesBehaviorService {

    /**
     * 存储喜欢数据
     * @param dto
     * @return
     */
    ResponseResult like(LikesBehaviorDto dto);

    ResponseResult unLike(UnLikesBehaviorDto dto);
}
