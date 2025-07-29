package com.zhanghongshen.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.wemedia.pojo.WmChannel;

import java.util.List;

public interface WmChannelService extends IService<WmChannel> {

    /**
     * 查询所有频道
     * @return
     */
    ResponseResult<List<WmChannel>> listAll();

}
