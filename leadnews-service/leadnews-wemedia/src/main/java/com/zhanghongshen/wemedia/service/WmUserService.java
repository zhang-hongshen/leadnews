package com.zhanghongshen.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.wemedia.dto.WmLoginDto;
import com.zhanghongshen.model.wemedia.pojo.WmUser;

public interface WmUserService extends IService<WmUser> {

    /**
     * 自媒体端登录
     * @param dto
     * @return
     */
    ResponseResult login(WmLoginDto dto);

}