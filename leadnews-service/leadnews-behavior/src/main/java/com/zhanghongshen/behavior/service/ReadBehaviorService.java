package com.zhanghongshen.behavior.service;

import com.zhanghongshen.model.behavior.dto.ReadBehaviorDto;
import com.zhanghongshen.common.dto.ResponseResult;

public interface ReadBehaviorService {

    /**
     * 保存阅读行为
     * @param dto
     * @return
     */
    ResponseResult readBehavior(ReadBehaviorDto dto);
}
