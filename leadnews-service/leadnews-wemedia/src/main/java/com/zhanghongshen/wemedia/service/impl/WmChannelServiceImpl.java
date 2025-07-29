package com.zhanghongshen.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.wemedia.pojo.WmChannel;
import com.zhanghongshen.wemedia.mapper.WmChannelMapper;
import com.zhanghongshen.wemedia.service.WmChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel>
        implements WmChannelService {


    /**
     * 查询所有频道
     * @return
     */
    @Override
    public ResponseResult<List<WmChannel>> listAll() {
        return ResponseResult.success(list());
    }
}
